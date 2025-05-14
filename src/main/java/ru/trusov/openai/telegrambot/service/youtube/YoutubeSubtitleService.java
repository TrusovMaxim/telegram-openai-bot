package ru.trusov.openai.telegrambot.service.youtube;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YoutubeSubtitleService {
    private static final String DOWNLOAD_DIR = "temp";

    public String extractSubtitles(String youtubeUrl, Long chatId) {
        var videoId = extractVideoId(youtubeUrl);
        var subtitleFilePrefix = chatId + "_" + videoId;
        var dir = Paths.get(DOWNLOAD_DIR);
        try {
            runYtDlp(youtubeUrl, chatId);
            Path subtitlePath = null;
            try (var stream = Files.newDirectoryStream(dir, subtitleFilePrefix + "*.vtt")) {
                for (var path : stream) {
                    subtitlePath = path;
                    break;
                }
            }
            if (subtitlePath == null) {
                log.error("Файл субтитров не найден для {}", subtitleFilePrefix);
                throw new FileNotFoundException("Файл субтитров не найден: " + subtitleFilePrefix);
            }
            return parseSubtitleFile(subtitlePath);
        } catch (Exception e) {
            log.error("Ошибка при получении субтитров: {}", e.getMessage(), e);
            return null;
        } finally {
            try (var stream = Files.newDirectoryStream(dir, subtitleFilePrefix + "*.vtt")) {
                for (var path : stream) {
                    Files.deleteIfExists(path);
                }
            } catch (IOException ioException) {
                log.warn("Не удалось удалить временные .vtt файлы: {}", ioException.getMessage());
            }
        }
    }

    private void runYtDlp(String youtubeUrl, Long chatId) throws IOException, InterruptedException {
        Files.createDirectories(Paths.get(DOWNLOAD_DIR));
        var outputPattern = DOWNLOAD_DIR + "/" + chatId + "_%(id)s.%(ext)s";
        var pb = new ProcessBuilder(
                "yt-dlp",
                "--write-auto-sub",
                "--sub-lang", "en,ru",
                "--skip-download",
                "-o", outputPattern,
                youtubeUrl
        );
        var process = pb.start();
        var exitCode = process.waitFor();
        if (exitCode != 0) {
            log.error("yt-dlp завершился с кодом {}", exitCode);
            throw new IOException("yt-dlp завершился с ошибкой, код: " + exitCode);
        }
    }

    private String parseSubtitleFile(Path path) throws IOException {
        var patternTime = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}\\.\\d{3} --> .*");
        var patternTag = Pattern.compile("<[^>]+>");
        try (var reader = Files.newBufferedReader(path)) {
            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("WEBVTT"))
                    .filter(line -> !line.startsWith("Kind:"))
                    .filter(line -> !patternTime.matcher(line).matches())
                    .map(line -> patternTag.matcher(line).replaceAll(""))
                    .collect(Collectors.joining(" "));
        }
    }

    private String extractVideoId(String url) {
        try {
            if (url.contains("v=")) {
                var start = url.indexOf("v=") + 2;
                var end = url.indexOf("&", start);
                return end != -1 ? url.substring(start, end) : url.substring(start);
            } else if (url.contains("youtu.be/")) {
                var start = url.indexOf("youtu.be/") + 9;
                var end = url.indexOf("?", start);
                return end != -1 ? url.substring(start, end) : url.substring(start);
            } else {
                throw new IllegalArgumentException("Неподдерживаемый формат ссылки: " + url);
            }
        } catch (Exception e) {
            log.error("Не удалось извлечь видео ID из URL: {}", url, e);
            throw new IllegalArgumentException("Невалидный YouTube URL");
        }
    }
}