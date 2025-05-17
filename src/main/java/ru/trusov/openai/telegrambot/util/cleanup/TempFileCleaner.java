package ru.trusov.openai.telegrambot.util.cleanup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;

@Slf4j
@Component
public class TempFileCleaner {
    private static final Path TEMP_DIR = Path.of("temp");
    private static final Duration FILE_TTL = Duration.ofMinutes(10);

    public void clearOldTempFiles() {
        try {
            if (!Files.exists(TEMP_DIR)) return;
            Files.list(TEMP_DIR)
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        try {
                            var lastModified = Files.getLastModifiedTime(p).toMillis();
                            var threshold = System.currentTimeMillis() - FILE_TTL.toMillis();
                            return lastModified < threshold;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                            log.debug("Удалён устаревший файл: {}", p);
                        } catch (IOException e) {
                            log.warn("Не удалось удалить файл: {}", p);
                        }
                    });
        } catch (IOException e) {
            log.error("Ошибка при очистке временных файлов: {}", e.getMessage(), e);
        }
    }
}