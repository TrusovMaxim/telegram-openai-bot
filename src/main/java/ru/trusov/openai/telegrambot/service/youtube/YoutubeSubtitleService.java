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
    private static final long MAX_SUBTITLE_FILE_SIZE = 20 * 1024 * 1024;

    public String extractSubtitles(String youtubeUrl, Long chatId) {
        var videoId = extractVideoId(youtubeUrl);
        var subtitleFilePrefix = chatId + "_" + videoId;
        var dir = Paths.get(DOWNLOAD_DIR);
        Path subtitlePath = null;
        try {
            Files.createDirectories(dir);
            for (int attempt = 1; attempt <= 3; attempt++) {
                runYtDlp(youtubeUrl, chatId);
                Thread.sleep(1000L * attempt);
                try (var stream = Files.newDirectoryStream(dir, subtitleFilePrefix + "*.vtt")) {
                    for (var path : stream) {
                        subtitlePath = path;
                        break;
                    }
                }
                if (subtitlePath != null) {
                    break;
                } else {
                    log.warn("Попытка {}: субтитры ещё не появились", attempt);
                }
            }
            if (subtitlePath == null) {
                log.error("Файл субтитров не найден для {}", subtitleFilePrefix);
                throw new FileNotFoundException("Файл субтитров не найден: " + subtitleFilePrefix);
            }
            if (Files.size(subtitlePath) > MAX_SUBTITLE_FILE_SIZE) {
                log.warn("Субтитры слишком большие: {} MB для {}", Files.size(subtitlePath) / 1024 / 1024, subtitleFilePrefix);
                throw new IOException("Видео слишком большое для обработки. Используйте /youtube на более короткое видео.");
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
                "--no-playlist",
                "--max-filesize", "100M",
                "--sub-format", "best",
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