package ru.trusov.openai.telegrambot.util.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.constant.BotSectionState;

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

    public <T> T executeLimited(Supplier<T> task, String taskName, Long chatId, Consumer<String> onWaitMessage) {
        var semaphore = limiters.getOrDefault(taskName, new Semaphore(5, true));
        try {
            var waiting = semaphore.getQueueLength();
            if (waiting > 0 && onWaitMessage != null) {
                onWaitMessage.accept(BotSectionState.STATE_WAITING_QUEUE_PREFIX + waiting + " задач перед вами.");
            }
            log.debug("Ожидание слота: {} (chatId={})", taskName, chatId);
            semaphore.acquire();
            log.debug("Выполняется: {} (chatId={})", taskName, chatId);
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Внутренняя ошибка. Попробуйте позже.");
        } finally {
            semaphore.release();
            log.debug("Завершено: {} (chatId={})", taskName, chatId);
        }
    }
}