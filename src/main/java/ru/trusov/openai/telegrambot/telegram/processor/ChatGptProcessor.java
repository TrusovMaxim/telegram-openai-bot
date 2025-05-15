package ru.trusov.openai.telegrambot.telegram.processor;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.config.openai.OpenAIClient;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.constant.BotPrompts;
import ru.trusov.openai.telegrambot.constant.BotSectionState;
import ru.trusov.openai.telegrambot.constant.BotWarnings;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserDataService;
import ru.trusov.openai.telegrambot.service.user.UserService;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class ChatGptProcessor {
    private final UserService userService;
    private final UserDataService userDataService;
    private final MessageSenderService messageSenderService;
    private final BuyImageProcessor buyImageProcessor;
    private static final int MAX_DIALOG_LENGTH = 40000;

    public void process(User user, Long chatId, String text, UserActionPathEnum action) {
        if (action == null) {
            messageSenderService.send(BotSectionState.STATE_REQUEST_SENT, chatId);
            var message = handleDialog(user, text);
            messageSenderService.send(message, chatId);
            return;
        }
        switch (action) {
            case CHAT_GPT -> messageSenderService.send(BotSectionState.STATE_CHAT_ALREADY_ACTIVATED, chatId);
            case TRANSLATOR -> {
                userService.updateBotStateEnum(user, BotStateEnum.TRANSLATOR);
                if (user.getSettingTranslator() == null) {
                    messageSenderService.sendTranslatorPrompt(chatId);
                } else {
                    messageSenderService.send(BotPrompts.PROMPT_VOICE_SEND, chatId);
                }
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
            case BUY_IMAGES -> buyImageProcessor.process(user, chatId);
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

    private String handleDialog(User user, String userText) {
        var data = userDataService.findByUser(user);
        if (data == null) {
            userDataService.createData(user, "");
            data = userDataService.findByUser(user);
        }
        var isPremium = Boolean.TRUE.equals(user.getIsPremium()) &&
                user.getPremiumEnd() != null &&
                LocalDateTime.now().isBefore(user.getPremiumEnd());
        var today = LocalDate.now();
        if (!isPremium) {
            if (data.getDialogDate() == null || !data.getDialogDate().isEqual(today)) {
                data.setDialogDate(today);
                data.setDialogUsageToday(0);
            }
            if (data.getDialogUsageToday() >= 20) {
                return BotWarnings.WARNING_GPT_DAILY_LIMIT_REACHED + "\n\n" + BotWarnings.PREMIUM_UPSELL;
            }
            data.setDialogUsageToday(data.getDialogUsageToday() + 1);
            userDataService.save(data);
            var prompt = "User: " + userText;
            return OpenAIClient.runOpenAI(prompt);
        }
        var currentData = data.getData();
        var updatedData = (currentData == null ? "" : currentData + "\n") + "User: " + userText;
        if (updatedData.length() > MAX_DIALOG_LENGTH) {
            data.setData(null);
            data.setCountData(0L);
            userDataService.save(data);
            return BotWarnings.WARNING_DIALOG_TOO_LONG;
        }
        var answer = OpenAIClient.runOpenAI(updatedData);
        updatedData += "\nBot: " + answer;
        data.setData(updatedData);
        data.setCountData(data.getCountData() + 1);
        var count = data.getCountData().intValue();
        if (count == 15) {
            data.setData(null);
            data.setCountData(0L);
            userDataService.save(data);
            return answer + BotWarnings.WARNING_GPT_DIALOG_RESET_NOTICE;
        }
        userDataService.save(data);
        if (count == 14) {
            return BotWarnings.WARNING_GPT_DIALOG_LAST_MESSAGE + answer;
        }
        return answer;
    }
}