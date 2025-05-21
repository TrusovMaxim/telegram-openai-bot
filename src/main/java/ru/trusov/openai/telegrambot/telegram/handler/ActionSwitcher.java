package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.constant.BotPrompts;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.telegram.processor.*;

import java.text.MessageFormat;

@Component
@AllArgsConstructor
public class ActionSwitcher {
    private final StartProcessor startProcessor;
    private final ChatGptProcessor chatGptProcessor;
    private final ImageProcessor imageProcessor;
    private final FeedbackProcessor feedbackProcessor;
    private final TranslatorProcessor translatorProcessor;
    private final YoutubeProcessor youtubeProcessor;
    private final MessageSenderService messageSenderService;
    private final UserService userService;

    public void route(User user, Long chatId, String text, UserActionPathEnum action) {
        if (action == null) {
            switch (user.getBotStateEnum()) {
                case START -> startProcessor.process(user, chatId, null);
                case CHAT_GPT -> chatGptProcessor.process(user, chatId, text, null);
                case IMAGE -> imageProcessor.process(user, chatId, text, null);
                case TRANSLATOR -> translatorProcessor.process(user, chatId, null);
                case YOUTUBE -> youtubeProcessor.process(user, chatId, text, null);
                case FEEDBACK -> feedbackProcessor.process(user, chatId, text, null);
            }
            return;
        }
        switch (action) {
            case SETTINGS -> {
                messageSenderService.sendSettingsMenu(chatId);
                return;
            }
            case BUY_IMAGES -> {
                if (user.getImageBalance() != null && user.getImageBalance() > 0) {
                    messageSenderService.send(
                            MessageFormat.format(BotMessages.MESSAGE_IMAGE_BALANCE_WARNING, user.getImageBalance()), chatId
                    );
                }
                messageSenderService.sendImageInvoice(chatId);
                return;
            }
            case BUY_PREMIUM -> {
                messageSenderService.sendPremiumInvoice(chatId);
                return;
            }
            case BALANCE -> {
                messageSenderService.send(
                        MessageFormat.format(BotMessages.MESSAGE_IMAGE_BALANCE_CURRENT, user.getImageBalance()), chatId
                );
                return;
            }
            case INFO -> {
                messageSenderService.send(BotMessages.MESSAGE_INFO_INTRO, chatId);
                return;
            }
            case FEEDBACK -> {
                userService.updateBotStateEnum(user, BotStateEnum.FEEDBACK);
                user.setBotStateEnum(BotStateEnum.FEEDBACK);
                messageSenderService.send(BotPrompts.PROMPT_FEEDBACK_WRITE, chatId);
                return;
            }
            case COMMANDS -> {
                messageSenderService.sendCommandMenu(chatId);
                return;
            }
            case DONATE -> {
                messageSenderService.send(BotMessages.MESSAGE_DONATE_INFO, chatId);
                return;
            }
            case ABOUT_AUTHOR -> {
                messageSenderService.send(BotMessages.MESSAGE_ABOUT_AUTHOR, chatId);
                return;
            }
        }
        switch (user.getBotStateEnum()) {
            case START -> startProcessor.process(user, chatId, action);
            case CHAT_GPT -> chatGptProcessor.process(user, chatId, text, action);
            case IMAGE -> imageProcessor.process(user, chatId, text, action);
            case TRANSLATOR -> translatorProcessor.process(user, chatId, action);
            case YOUTUBE -> youtubeProcessor.process(user, chatId, text, action);
            case FEEDBACK -> feedbackProcessor.process(user, chatId, text, action);
        }
    }
}