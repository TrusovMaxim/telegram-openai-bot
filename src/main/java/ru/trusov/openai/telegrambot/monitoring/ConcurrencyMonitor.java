package ru.trusov.openai.telegrambot.monitoring;

import lombok.extern.slf4j.Slf4j;
import ru.trusov.openai.telegrambot.logging.LogMessages;

@Slf4j
public class ConcurrencyMonitor {

    public static long start(String taskType) {
        log.debug("🔄 Старт задачи [{}]", taskType);
        return System.currentTimeMillis();
    }

    public static void finish(String taskType, long startTime, Long userId, Long chatId) {
        var duration = System.currentTimeMillis() - startTime;
        log.info(LogMessages.TIME_SPENT, taskType, duration, userId, chatId);
        logMemoryUsage(taskType, userId, chatId);
    }

    public static void logQueueFull(String type, Long userId, Long chatId) {
        log.warn(LogMessages.QUEUE_FULL, type, userId, chatId);
    }

    public static void logAcquired(String type, Long userId, Long chatId) {
        log.debug(LogMessages.ACQUIRED, type, userId, chatId);
    }

    public static void logReleased(String type, Long userId, Long chatId) {
        log.debug(LogMessages.RELEASED, type, userId, chatId);
    }

    private static void logMemoryUsage(String type, Long userId, Long chatId) {
        var usedMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        log.debug(LogMessages.MEMORY_USAGE, type, usedMB, userId, chatId);
    }
}