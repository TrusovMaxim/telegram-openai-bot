package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.telegram.processor.TranslatorProcessor;

@Component
@AllArgsConstructor
public class VoiceMessageHandler {
    private final TranslatorProcessor translatorProcessor;
    private final MessageSenderService messageSenderService;

    public void handle(Update update, User user) {
        var voice = update.getMessage().getVoice();
        var chatId = update.getMessage().getChatId();
        if (user != null && user.getBotStateEnum() == BotStateEnum.TRANSLATOR) {
            translatorProcessor.process(user, voice, chatId);
        } else {
            messageSenderService.send(BotErrors.ERROR_UNSUPPORTED_MESSAGE, chatId);
        }
    }
}