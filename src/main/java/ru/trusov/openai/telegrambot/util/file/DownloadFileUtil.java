package ru.trusov.openai.telegrambot.util.file;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DownloadFileUtil {

    public static File download(String fileUrl, Long chatId) {
        try (var in = new URL(fileUrl).openStream()) {
            var fileName = "temp/" + chatId + "_" + System.nanoTime();
            var path = Paths.get(fileName);
            Files.createDirectories(path.getParent());
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            return path.toFile();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить файл", e);
        }
    }
}