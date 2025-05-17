package ru.trusov.openai.telegrambot.util.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class DownloadFileVoice {
    private static final String AUDIO_DIR = "temp";

    public File download(String urlVoice, Long chatId) throws IOException, InterruptedException {
        Files.createDirectories(Path.of(AUDIO_DIR));
        var uniquePrefix = chatId + "_" + System.nanoTime();
        var sourcePath = Path.of(AUDIO_DIR, uniquePrefix + "_source.oga");
        var targetPath = Path.of(AUDIO_DIR, uniquePrefix + "_compressed.mp3");
        FileUtils.copyURLToFile(new URL(urlVoice), sourcePath.toFile());
        var builder = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", sourcePath.toString(),
                "-b:a", "64k",
                "-ac", "1",
                "-ar", "16000",
                targetPath.toString()
        );
        builder.redirectErrorStream(true);
        var process = builder.start();
        var exitCode = process.waitFor();
        if (exitCode != 0 || !targetPath.toFile().exists()) {
            throw new IOException("Ошибка при конвертации аудио в mp3.");
        }
        Files.deleteIfExists(sourcePath);
        return targetPath.toFile();
    }
}