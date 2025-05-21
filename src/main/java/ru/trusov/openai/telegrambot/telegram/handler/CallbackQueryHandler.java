package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trusov.openai.telegrambot.constant.BotOptions;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.enums.TranslatorTypeEnum;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.telegram.processor.ImageProcessor;
import ru.trusov.openai.telegrambot.telegram.processor.TranslatorProcessor;

@Component
@AllArgsConstructor
public class CallbackQueryHandler {
    private final TranslatorProcessor translatorProcessor;
    private final ImageProcessor imageProcessor;
    private final UserService userService;
    private final MessageSenderService messageSenderService;
    private final ActionSwitcher actionSwitcher;

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
            case BotOptions.OPTION_IMAGE_SIZE_SQUARE ->
                    imageProcessor.setSize(user, chatId, messageId, ImageSizeEnum.SQUARE);
            case BotOptions.OPTION_IMAGE_SIZE_VERTICAL ->
                    imageProcessor.setSize(user, chatId, messageId, ImageSizeEnum.VERTICAL);
            case BotOptions.OPTION_IMAGE_SIZE_HORIZONTAL ->
                    imageProcessor.setSize(user, chatId, messageId, ImageSizeEnum.HORIZONTAL);
            case BotOptions.OPTION_VOICE_SETTINGS -> translatorProcessor.editSettings(chatId, messageId);
            case BotOptions.OPTION_IMAGE_SETTINGS -> imageProcessor.editSettings(chatId, messageId);
            case BotOptions.OPTION_BACK_TO_SETTINGS_MENU -> messageSenderService.editSettingsMenu(chatId, messageId);
            default -> {
                if (data.startsWith("/")) {
                    var action = UserActionPathEnum.parse(data);
                    actionSwitcher.route(user, chatId, data, action);
                    messageSenderService.deleteMessage(chatId, messageId);
                }
            }
        }
    }
}