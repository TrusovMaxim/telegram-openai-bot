package ru.trusov.openai.telegrambot.telegram.processor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.constant.BotErrors;
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
import ru.trusov.openai.telegrambot.util.file.ConcurrencyLimiter;

import java.text.MessageFormat;

@Slf4j
@Component
@AllArgsConstructor
public class ImageProcessor {
    private final UserService userService;
    private final UserDataService userDataService;
    private final MessageSenderService messageSenderService;
    private final ImageService imageService;
    private final ConcurrencyLimiter concurrencyLimiter;

    public void process(User user, Long chatId, String text, UserActionPathEnum action) {
        if (action == null) {
            if (user.getSettingImage() == null) {
                messageSenderService.sendImagePrompt(chatId);
            } else {
                if (user.getImageBalance() == null || user.getImageBalance() <= 0) {
                    messageSenderService.send(BotMessages.MESSAGE_NO_IMAGE_BALANCE, chatId);
                    return;
                }
                user.setImageBalance(user.getImageBalance() - 1);
                userService.save(user);
                generate(user, chatId, text);
            }
            return;
        }
        switch (action) {
            case CHAT_GPT -> {
                userService.updateBotStateEnum(user, BotStateEnum.CHAT_GPT);
                messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_GPT, chatId);
            }
            case RESET_GPT_DIALOG -> {
                userService.updateBotStateEnum(user, BotStateEnum.CHAT_GPT);
                userDataService.resetUserDialog(user);
                messageSenderService.send(BotSectionState.STATE_CHAT_GPT_DIALOG_RESET, chatId);
            }
            case TRANSLATOR -> {
                userService.updateBotStateEnum(user, BotStateEnum.TRANSLATOR);
                if (user.getSettingTranslator() == null) {
                    messageSenderService.sendTranslatorPrompt(chatId);
                } else {
                    messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_TRANSLATOR, chatId);
                }
            }
            case YOUTUBE -> {
                userService.updateBotStateEnum(user, BotStateEnum.YOUTUBE);
                messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_YOUTUBE, chatId);
            }
            case IMAGE -> messageSenderService.send(BotSectionState.STATE_CHAT_ALREADY_IN_SECTION, chatId);
            case BALANCE -> messageSenderService.send(
                    MessageFormat.format(BotMessages.MESSAGE_IMAGE_BALANCE_CURRENT, user.getImageBalance()), chatId);
            case INFO -> messageSenderService.sendInfoWithButtons(chatId);
            case FEEDBACK -> {
                userService.updateBotStateEnum(user, BotStateEnum.FEEDBACK);
                messageSenderService.send(BotPrompts.PROMPT_FEEDBACK_WRITE, chatId);
            }
            case SETTING_VOICE -> messageSenderService.sendTranslatorPrompt(chatId);
            case SETTING_IMAGE -> messageSenderService.sendImagePrompt(chatId);
            case DONATE -> messageSenderService.send(BotMessages.MESSAGE_DONATE_INFO, chatId);
            case ABOUT_AUTHOR -> messageSenderService.send(BotMessages.MESSAGE_ABOUT_AUTHOR, chatId);
        }
    }

    public void setSize(User user, Long chatId, Integer messageId, ImageSizeEnum size) {
        userService.updateSettingImageEnum(user, size);
        userService.updateBotStateEnum(user, BotStateEnum.IMAGE);
        var msg = switch (size) {
            case SQUARE -> BotSectionState.STATE_CHOICE_IMAGE_SIZE_SQUARE;
            case VERTICAL -> BotSectionState.STATE_CHOICE_IMAGE_SIZE_VERTICAL;
            case HORIZONTAL -> BotSectionState.STATE_CHOICE_IMAGE_SIZE_HORIZONTAL;
        };
        messageSenderService.edit(msg, chatId, messageId);
    }

    private void generate(User user, Long chatId, String prompt) {
        var userId = user.getId();
        var taskType = "image_generation";
        concurrencyLimiter.executeLimited(() -> {
            try {
                messageSenderService.send(BotSectionState.STATE_REQUEST_IMAGE_SENT, chatId);
                var url = imageService.generate(user.getSettingImage(), prompt);
                messageSenderService.sendImageLink(url, chatId);
            } catch (Exception e) {
                log.error("Ошибка генерации изображения: chatId={}, userId={}, error={}", chatId, userId, e.getMessage(), e);
                messageSenderService.send(BotErrors.ERROR_IMAGE_GENERATION_FAILED, chatId);
            }
            return null;
        }, taskType, userId, chatId, msg -> messageSenderService.send(msg, chatId));
    }

    public void editSettings(Long chatId, Integer messageId) {
        messageSenderService.editImageSettings(chatId, messageId);
    }
}