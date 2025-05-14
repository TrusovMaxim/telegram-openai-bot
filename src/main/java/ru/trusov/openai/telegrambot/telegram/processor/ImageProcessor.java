package ru.trusov.openai.telegrambot.telegram.processor;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.constant.BotPrompts;
import ru.trusov.openai.telegrambot.constant.BotSectionState;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.ImageService;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserDataService;
import ru.trusov.openai.telegrambot.service.user.UserService;

@Component
@AllArgsConstructor
public class ImageProcessor {
    private final UserService userService;
    private final UserDataService userDataService;
    private final MessageSenderService messageSenderService;
    private final ImageService imageService;

    public void process(User user, Long chatId, String text, UserActionPathEnum action) {
        if (action == null) {
            if (user.getSettingImage() == null) {
                messageSenderService.sendImagePrompt(chatId);
            } else {
                generate(user, chatId, text);
            }
            return;
        }
        switch (action) {
            case IMAGE -> messageSenderService.send(BotSectionState.STATE_CHAT_ALREADY_IN_SECTION, chatId);
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
            case YOUTUBE -> {
                userService.updateBotStateEnum(user, BotStateEnum.YOUTUBE);
                messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_YOUTUBE, chatId);
            }
            case INFO -> {
                userService.updateBotStateEnum(user, BotStateEnum.CHAT_GPT);
                messageSenderService.send(BotMessages.MESSAGE_INFO_INTRO, chatId);
            }
            case FEEDBACK -> {
                userService.updateBotStateEnum(user, BotStateEnum.FEEDBACK);
                messageSenderService.send(BotPrompts.PROMPT_FEEDBACK_WRITE, chatId);
            }
            case SETTING_VOICE -> messageSenderService.sendTranslatorPrompt(chatId);
            case SETTING_IMAGE -> messageSenderService.sendImagePrompt(chatId);
            case COMMANDS -> messageSenderService.send(BotMessages.MESSAGE_COMMAND_LIST, chatId);
            case DONATE -> messageSenderService.send(BotMessages.MESSAGE_DONATE_INFO, chatId);
            case ABOUT_AUTHOR -> messageSenderService.send(BotMessages.MESSAGE_ABOUT_AUTHOR, chatId);
        }
    }

    public void setSize(User user, Long chatId, Integer messageId, ImageSizeEnum size) {
        userService.updateSettingImageEnum(user, size);
        userService.updateBotStateEnum(user, BotStateEnum.IMAGE);
        var msg = switch (size) {
            case MINIMUM_SIZE -> BotSectionState.STATE_CHOICE_IMAGE_SIZE_SMALL;
            case AVERAGE_SIZE -> BotSectionState.STATE_CHOICE_IMAGE_SIZE_MEDIUM;
            case BIG_SIZE -> BotSectionState.STATE_CHOICE_IMAGE_SIZE_LARGE;
        };
        messageSenderService.edit(msg, chatId, messageId);
    }

    private void generate(User user, Long chatId, String prompt) {
        messageSenderService.send(BotSectionState.STATE_REQUEST_IMAGE_SENT, chatId);
        var url = imageService.generate(user.getSettingImage(), prompt);
        messageSenderService.sendImageLink(url, chatId);
    }
}