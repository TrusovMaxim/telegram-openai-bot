package ru.trusov.openai.telegrambot.telegram.processor;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Voice;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.constant.BotPrompts;
import ru.trusov.openai.telegrambot.constant.BotSectionState;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.TranslatorTypeEnum;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.bot.TranslatorService;
import ru.trusov.openai.telegrambot.service.user.UserDataService;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.util.file.ConcurrencyLimiter;

import java.text.MessageFormat;

@Component
@AllArgsConstructor
public class TranslatorProcessor {
    private final UserService userService;
    private final UserDataService userDataService;
    private final MessageSenderService messageSenderService;
    private final TranslatorService translatorService;
    private final ConcurrencyLimiter concurrencyLimiter;

    public void process(User user, Voice voice, Long chatId) {
        if (voice.getDuration() > 600) {
            messageSenderService.send(BotErrors.ERROR_VOICE_MESSAGE_TOO_LONG, chatId);
        } else if (user.getSettingTranslator() == null) {
            userService.updateBotStateEnum(user, BotStateEnum.TRANSLATOR);
            messageSenderService.sendTranslatorPrompt(chatId);
        } else {
            var taskType = "voice";
            var userId = user.getId();
            concurrencyLimiter.executeLimited(() -> {
                var responseText = translatorService.translate(user.getSettingTranslator(), voice.getFileId(), chatId);
                messageSenderService.send(responseText, chatId);
                return null;
            }, taskType, userId, chatId, msg -> messageSenderService.send(msg, chatId));
        }
    }

    public void process(User user, Long chatId, UserActionPathEnum action) {
        if (action == null) {
            messageSenderService.send(BotErrors.ERROR_VOICE_REQUIRED, chatId);
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
            case TRANSLATOR -> messageSenderService.send(BotSectionState.STATE_CHAT_ALREADY_IN_SECTION, chatId);
            case YOUTUBE -> {
                userService.updateBotStateEnum(user, BotStateEnum.YOUTUBE);
                messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_YOUTUBE, chatId);
            }
            case IMAGE -> {
                userService.updateBotStateEnum(user, BotStateEnum.IMAGE);
                if (user.getSettingImage() == null) {
                    messageSenderService.sendImagePrompt(chatId);
                } else {
                    messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_IMAGE, chatId);
                }
            }
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

    public void setMode(User user, Long chatId, Integer messageId, TranslatorTypeEnum type) {
        userService.updateSettingTranslatorEnum(user, type);
        userService.updateBotStateEnum(user, BotStateEnum.TRANSLATOR);
        var msg = (type == TranslatorTypeEnum.TRANSLATION)
                ? BotSectionState.STATE_CHOICE_TRANSLATION_RESPONSE
                : BotSectionState.STATE_CHOICE_TRANSCRIPTION_RESPONSE;
        messageSenderService.edit(msg, chatId, messageId);
    }

    public void editSettings(Long chatId, Integer messageId) {
        messageSenderService.editVoiceSettings(chatId, messageId);
    }
}