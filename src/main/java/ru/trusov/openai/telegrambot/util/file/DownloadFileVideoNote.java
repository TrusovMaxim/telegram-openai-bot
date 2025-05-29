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
public class DownloadFileVideoNote {
    private static final String VIDEO_DIR = "temp";

    public File download(String urlVideoNote, Long chatId) throws IOException, InterruptedException {
        Files.createDirectories(Path.of(VIDEO_DIR));
        var uniquePrefix = chatId + "_" + System.nanoTime();
        var sourcePath = Path.of(VIDEO_DIR, uniquePrefix + "_source.mp4");
        var targetPath = Path.of(VIDEO_DIR, uniquePrefix + "_audio.mp3");
        FileUtils.copyURLToFile(new URL(urlVideoNote), sourcePath.toFile());
        var builder = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", sourcePath.toString(),
                "-vn",
                "-b:a", "64k",
                "-ac", "1",
                "-ar", "16000",
                targetPath.toString()
        );
        builder.redirectErrorStream(true);
        var process = builder.start();
        var exitCode = process.waitFor();
        if (exitCode != 0 || !targetPath.toFile().exists()) {
            throw new IOException("Ошибка при извлечении аудио из кружка.");
        }
        Files.deleteIfExists(sourcePath);
        return targetPath.toFile();
    }
}