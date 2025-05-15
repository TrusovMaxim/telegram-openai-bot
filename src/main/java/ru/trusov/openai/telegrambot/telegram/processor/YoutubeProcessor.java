package ru.trusov.openai.telegrambot.telegram.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.constant.*;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.openai.impl.OpenAIClientApiServiceImpl;
import ru.trusov.openai.telegrambot.service.user.UserDataService;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.service.youtube.YoutubeSubtitleService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class YoutubeProcessor {
    private final UserService userService;
    private final UserDataService userDataService;
    private final MessageSenderService messageSenderService;
    private final YoutubeSubtitleService subtitleService;
    private final OpenAIClientApiServiceImpl openAIClientApiService;

    public void process(User user, Long chatId, String text, UserActionPathEnum action) {
        if (action != null) {
            switchSection(user, chatId, action);
            return;
        }
        if (!isValidYoutubeUrl(text)) {
            messageSenderService.send(BotErrors.ERROR_YOUTUBE_INVALID_URL, chatId);
            return;
        }
        if (!hasYoutubeQuota(user)) {
            messageSenderService.send(BotWarnings.WARNING_YOUTUBE_LIMIT_REACHED, chatId);
            return;
        }
        handleYoutubeUrl(chatId, text);
    }

    private void switchSection(User user, Long chatId, UserActionPathEnum action) {
        switch (action) {
            case YOUTUBE -> messageSenderService.send(BotSectionState.STATE_CHAT_ALREADY_IN_SECTION, chatId);
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

    private boolean hasYoutubeQuota(User user) {
        var now = LocalDate.now();
        if (Boolean.TRUE.equals(user.getIsPremium()) && user.getPremiumEnd() != null &&
                LocalDateTime.now().isBefore(user.getPremiumEnd())) {
            return true;
        }
        if (user.getYoutubeUsageDate() == null || !user.getYoutubeUsageDate().isEqual(now)) {
            user.setYoutubeUsageDate(now);
            user.setYoutubeUsageToday(0);
        }
        if (user.getYoutubeUsageToday() >= 1) {
            return false;
        }
        user.setYoutubeUsageToday(user.getYoutubeUsageToday() + 1);
        userService.save(user);
        return true;
    }

    public void handleYoutubeUrl(Long chatId, String messageText) {
        try {
            var url = messageText.replace("/youtube", "").trim();
            messageSenderService.send(BotSectionState.STATE_YOUTUBE_SUBTITLE_LOADING, chatId);
            var subtitles = subtitleService.extractSubtitles(url, chatId);
            if (subtitles == null || subtitles.isBlank()) {
                messageSenderService.send(BotErrors.ERROR_YOUTUBE_SUBTITLE_NOT_FOUND, chatId);
                return;
            }
            messageSenderService.send(BotSectionState.STATE_YOUTUBE_SUMMARIZING, chatId);
            var summary = openAIClientApiService.summarizeText(subtitles);
            messageSenderService.send(summary, chatId);
        } catch (Exception e) {
            log.error("Ошибка при обработке YouTube-видео: {}", e.getMessage(), e);
            messageSenderService.send(BotErrors.ERROR_YOUTUBE_PROCESSING, chatId);
        }
    }

    private boolean isValidYoutubeUrl(String url) {
        return url != null && (url.contains("youtu.be/") || url.contains("youtube.com/watch?v="));
    }
}