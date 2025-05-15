package ru.trusov.openai.telegrambot.constant;

public interface BotWarnings {
    String WARNING_GPT_DIALOG_LAST_MESSAGE = "⚠️ Осталось одно сообщение в рамках этого диалога. Далее будет сброс истории.\n\n";
    String WARNING_GPT_DIALOG_RESET_NOTICE = "\n\n🔄 История сброшена. Следующее сообщение начнёт новый диалог.";
    String WARNING_DIALOG_TOO_LONG = "⚠️ Диалог оказался слишком длинным и был автоматически сброшен. Продолжим с чистого листа.";
    String WARNING_VOICE_LIMIT_REACHED = "🛑 Лимит голосовых сообщений (10 в день) исчерпан.\nОформите премиум для безлимитного использования.";
    String WARNING_YOUTUBE_LIMIT_REACHED = "🛑 Вы можете обрабатывать только 1 видео в день.\nПодключите премиум для снятия ограничения.";
    String WARNING_GPT_DAILY_LIMIT_REACHED = "🛑 Вы использовали лимит из 20 сообщений на сегодня.\nПопробуйте завтра или оформите премиум.";
    String PREMIUM_UPSELL = "🔓 Оформите премиум — и получите полный доступ ко всем функциям без ограничений.";
}