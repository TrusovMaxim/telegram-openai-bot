package ru.trusov.openai.telegrambot.telegram.processor;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.constant.BotPrompts;
import ru.trusov.openai.telegrambot.constant.BotSectionState;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserDataService;
import ru.trusov.openai.telegrambot.service.feedback.UserFeedbackService;
import ru.trusov.openai.telegrambot.service.user.UserService;

import java.text.MessageFormat;

@Component
@AllArgsConstructor
public class FeedbackProcessor {
    private final UserService userService;
    private final UserDataService userDataService;
    private final UserFeedbackService userFeedbackService;
    private final MessageSenderService messageSenderService;

    public void process(User user, Long chatId, String text, UserActionPathEnum action) {
        if (action == null) {
            userService.updateBotStateEnum(user, BotStateEnum.CHAT_GPT);
            userFeedbackService.saveFeedback(text, user);
            messageSenderService.send(BotMessages.MESSAGE_FEEDBACK_THANKS, chatId);
            return;
        }
        switch (action) {
            case FEEDBACK -> messageSenderService.send(BotSectionState.STATE_CHAT_ALREADY_IN_SECTION, chatId);
            case TRANSLATOR -> {
                userService.updateBotStateEnum(user, BotStateEnum.TRANSLATOR);
                if (user.getSettingTranslator() == null) {
                    messageSenderService.sendTranslatorPrompt(chatId);
                } else {
                    messageSenderService.send(BotPrompts.PROMPT_VOICE_SEND, chatId);
                }
            }
            case CHAT_GPT -> {
                userService.updateBotStateEnum(user, BotStateEnum.CHAT_GPT);
                messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_GPT, chatId);
            }
            case RESET_GPT_DIALOG -> {
                userService.updateBotStateEnum(user, BotStateEnum.CHAT_GPT);
                userDataService.resetUserDialog(user);
                messageSenderService.send(BotSectionState.STATE_CHAT_GPT_DIALOG_RESET + BotSectionState.STATE_CHAT_SWITCHED_TO_GPT, chatId);
            }
            case IMAGE -> {
                userService.updateBotStateEnum(user, BotStateEnum.IMAGE);
                if (user.getSettingImage() == null) {
                    messageSenderService.sendImagePrompt(chatId);
                } else {
                    messageSenderService.send(BotPrompts.PROMPT_IMAGE_DESCRIPTION_REQUEST, chatId);
                }
            }
            case BALANCE ->
                    messageSenderService.send(MessageFormat.format(BotMessages.MESSAGE_IMAGE_BALANCE_CURRENT, user.getImageBalance()), chatId);
            case YOUTUBE -> {
                userService.updateBotStateEnum(user, BotStateEnum.YOUTUBE);
                messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_YOUTUBE, chatId);
            }
            case INFO -> {
                userService.updateBotStateEnum(user, BotStateEnum.CHAT_GPT);
                messageSenderService.send(BotMessages.MESSAGE_INFO_INTRO, chatId);
            }
            case SETTING_VOICE -> messageSenderService.sendTranslatorPrompt(chatId);
            case SETTING_IMAGE -> messageSenderService.sendImagePrompt(chatId);
            case COMMANDS -> messageSenderService.send(BotMessages.MESSAGE_COMMAND_LIST, chatId);
            case DONATE -> messageSenderService.send(BotMessages.MESSAGE_DONATE_INFO, chatId);
            case ABOUT_AUTHOR -> messageSenderService.send(BotMessages.MESSAGE_ABOUT_AUTHOR, chatId);
        }
    }
}