package ru.trusov.openai.telegrambot.util.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.constant.BotSectionState;
import ru.trusov.openai.telegrambot.monitoring.ConcurrencyMonitor;

import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Component
public class ConcurrencyLimiter {
    private final Map<String, Semaphore> limiters = Map.of(
            "chat_gpt", new Semaphore(20, true),
            "voice", new Semaphore(10, true),
            "youtube", new Semaphore(10, true),
            "image_generation", new Semaphore(10, true)
    );

    public <T> T executeLimited(Supplier<T> task, String taskName, Long userId, Long chatId, Consumer<String> onWaitMessage) {
        var semaphore = limiters.getOrDefault(taskName, new Semaphore(5, true));
        var start = ConcurrencyMonitor.start(taskName);
        try {
            var waiting = semaphore.getQueueLength();
            if (waiting > 0 && onWaitMessage != null) {
                onWaitMessage.accept(BotSectionState.STATE_WAITING_QUEUE_PREFIX + waiting + " задач перед вами.");
            }
            var acquired = semaphore.tryAcquire();
            if (!acquired) {
                ConcurrencyMonitor.logQueueFull(taskName, userId, chatId);
                if (onWaitMessage != null) {
                    onWaitMessage.accept("⏳ Сейчас слишком много запросов. Пожалуйста, подождите.");
                }
                return null;
            }
            ConcurrencyMonitor.logAcquired(taskName, userId, chatId);
            return task.get();
        } catch (Exception e) {
            throw new RuntimeException("Внутренняя ошибка. Попробуйте позже.", e);
        } finally {
            semaphore.release();
            ConcurrencyMonitor.logReleased(taskName, userId, chatId);
            ConcurrencyMonitor.finish(taskName, start, userId, chatId);
        }
    }
}