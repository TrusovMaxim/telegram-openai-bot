package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.constant.BotWarnings;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.telegram.processor.TranslatorProcessor;

import java.time.LocalDateTime;

import static java.time.LocalDate.*;

@Component
@AllArgsConstructor
public class VoiceMessageHandler {
    private final TranslatorProcessor translatorProcessor;
    private final MessageSenderService messageSenderService;
    private final UserService userService;

    public void handle(Update update, User user) {
        var voice = update.getMessage().getVoice();
        var chatId = update.getMessage().getChatId();
        if (user == null || user.getBotStateEnum() != BotStateEnum.TRANSLATOR) {
            messageSenderService.send(BotErrors.ERROR_UNSUPPORTED_MESSAGE, chatId);
            return;
        }
        var today = now();
        var isPremium = Boolean.TRUE.equals(user.getIsPremium()) &&
                user.getPremiumEnd() != null &&
                LocalDateTime.now().isBefore(user.getPremiumEnd());
        if (!isPremium) {
            if (user.getVoiceUsageDate() == null || !user.getVoiceUsageDate().isEqual(today)) {
                user.setVoiceUsageDate(today);
                user.setVoiceUsageToday(0);
            }
            if (user.getVoiceUsageToday() >= 10) {
                messageSenderService.send(BotWarnings.WARNING_VOICE_LIMIT_REACHED, chatId);
                return;
            }
            user.setVoiceUsageToday(user.getVoiceUsageToday() + 1);
        }
        userService.save(user);
        translatorProcessor.process(user, voice, chatId);
    }
}