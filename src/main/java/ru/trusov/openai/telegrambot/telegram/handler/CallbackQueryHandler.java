package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trusov.openai.telegrambot.constant.BotOptions;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.enums.TranslatorTypeEnum;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.telegram.processor.ImageProcessor;
import ru.trusov.openai.telegrambot.telegram.processor.TranslatorProcessor;

@Component
@AllArgsConstructor
public class CallbackQueryHandler {
    private final TranslatorProcessor translatorProcessor;
    private final ImageProcessor imageProcessor;
    private final UserService userService;

    public void handle(Update update) {
        var callback = update.getCallbackQuery();
        var user = userService.getUser(callback.getMessage().getChatId());
        var data = callback.getData();
        var chatId = callback.getMessage().getChatId();
        var messageId = callback.getMessage().getMessageId();
        switch (data) {
            case BotOptions.OPTION_VOICE_TRANSCRIPTION ->
                    translatorProcessor.setMode(user, chatId, messageId, TranslatorTypeEnum.TRANSCRIPTION);
            case BotOptions.OPTION_VOICE_TRANSLATION ->
                    translatorProcessor.setMode(user, chatId, messageId, TranslatorTypeEnum.TRANSLATION);
            case BotOptions.OPTION_IMAGE_SIZE_MINIMUM ->
                    imageProcessor.setSize(user, chatId, messageId, ImageSizeEnum.MINIMUM_SIZE);
            case BotOptions.OPTION_IMAGE_SIZE_AVERAGE ->
                    imageProcessor.setSize(user, chatId, messageId, ImageSizeEnum.AVERAGE_SIZE);
            case BotOptions.OPTION_IMAGE_SIZE_LARGE ->
                    imageProcessor.setSize(user, chatId, messageId, ImageSizeEnum.BIG_SIZE);
        }
    }
}