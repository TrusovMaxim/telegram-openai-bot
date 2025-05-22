package ru.trusov.openai.telegrambot.logging;

public interface LogMessages {
    String QUEUE_FULL = "❗ Очередь переполнена для [{}] — userId={}, chatId={}";
    String ACQUIRED = "✅ Ресурс захвачен: [{}] — userId={}, chatId={}";
    String RELEASED = "🔓 Ресурс освобождён: [{}] — userId={}, chatId={}";
    String TIME_SPENT = "🕓 Задача [{}] завершена за {} мс — userId={}, chatId={}";
    String MEMORY_USAGE = "💾 Использовано памяти после [{}]: {} МБ — userId={}, chatId={}";
}