package ru.trusov.openai.telegrambot.util.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempCleanupScheduler {
    private final TempFileCleaner tempFileCleaner;

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    public void scheduleCleanup() {
        log.debug("🧹 Запущена периодическая очистка временных файлов...");
        tempFileCleaner.clearOldTempFiles();
    }
}