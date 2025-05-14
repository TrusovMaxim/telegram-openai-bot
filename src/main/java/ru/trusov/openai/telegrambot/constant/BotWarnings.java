package ru.trusov.openai.telegrambot.constant;

public interface BotWarnings {
    String WARNING_GPT_DIALOG_LAST_MESSAGE = "Осталось одно сообщение в рамках этого диалога, далее будет сброс.\n\n";
    String WARNING_GPT_DIALOG_RESET_NOTICE = "\n\nСброс диалога, следующее сообщение начнет новый диалог.";
    String WARNING_DIALOG_TOO_LONG = "Диалог оказался слишком длинным и был автоматически сброшен. Продолжим с чистого листа.";
}