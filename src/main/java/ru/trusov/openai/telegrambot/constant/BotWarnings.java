package ru.trusov.openai.telegrambot.constant;

public interface BotWarnings {
    String WARNING_GPT_DIALOG_LAST_MESSAGE = "⚠️ Осталось одно сообщение в рамках этого диалога. Далее будет сброс истории.\n\n";
    String WARNING_GPT_DIALOG_RESET_NOTICE = "\n\n🔄 История сброшена. Следующее сообщение начнёт новый диалог.";
    String WARNING_DIALOG_TOO_LONG = "⚠️ Диалог оказался слишком длинным и был автоматически сброшен. Продолжим с чистого листа.";
    String WARNING_VOICE_LIMIT_REACHED = """
            🛑 Лимит голосовых сообщений (10 в день) исчерпан.
            
            🔓 Оформите премиум — получите безлимитный доступ к голосовым функциям и другим возможностям.
            
            👉 /activate_premium
            """;
    String WARNING_YOUTUBE_LIMIT_REACHED = """
            🛑 Вы можете обрабатывать только 1 видео в день.
            
            🔓 Премиум снимает это ограничение и позволяет обрабатывать неограниченное количество видео.
            
            👉 /activate_premium
            """;
    String WARNING_GPT_DAILY_LIMIT_REACHED = """
            🛑 Вы использовали лимит из 20 сообщений на сегодня.
            
            🔓 Премиум открывает безлимитное общение с GPT и включает сохранение истории диалога.
            
            👉 /activate_premium
            """;
}