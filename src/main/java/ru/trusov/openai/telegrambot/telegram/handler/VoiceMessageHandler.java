package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.constant.BotWarnings;
import ru.trusov.openai.telegrambot.model.dto.record.UsageLimitCheckParams;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.telegram.processor.TranslatorProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        var proceed = checkAndUpdateUsageLimit(new UsageLimitCheckParams(
                user,
                user.getVoiceUsageDate(),
                10,
                user::setVoiceUsageDate,
                user::setVoiceUsageToday,
                user::getVoiceUsageToday,
                BotWarnings.WARNING_VOICE_LIMIT_REACHED,
                chatId
        ));
        if (!proceed) return;
        userService.save(user);
        translatorProcessor.process(user, voice, chatId);
    }

    public void handleVideoNote(Update update, User user) {
        var videoNote = update.getMessage().getVideoNote();
        var chatId = update.getMessage().getChatId();
        if (user == null || user.getBotStateEnum() != BotStateEnum.TRANSLATOR) {
            messageSenderService.send(BotErrors.ERROR_UNSUPPORTED_MESSAGE, chatId);
            return;
        }
        var proceed = checkAndUpdateUsageLimit(new UsageLimitCheckParams(
                user,
                user.getVideoNoteUsageDate(),
                1,
                user::setVideoNoteUsageDate,
                user::setVideoNoteUsageToday,
                user::getVideoNoteUsageToday,
                BotWarnings.WARNING_VIDEO_NOTE_LIMIT_REACHED,
                chatId
        ));
        if (!proceed) return;
        userService.save(user);
        translatorProcessor.processVideoNote(user, videoNote, chatId);
    }

    private boolean checkAndUpdateUsageLimit(UsageLimitCheckParams params) {
        var today = LocalDate.now();
        if (params.user().getIsPremium() != null &&
                params.user().getIsPremium() &&
                params.user().getPremiumEnd() != null &&
                LocalDateTime.now().isBefore(params.user().getPremiumEnd())) {
            return true;
        }
        if (params.usageDate() == null || !params.usageDate().isEqual(today)) {
            params.dateSetter().accept(today);
            params.counterSetter().accept(0);
        }
        if (params.counterGetter().get() >= params.limit()) {
            messageSenderService.send(params.warningMessage(), params.chatId());
            return false;
        }
        params.counterSetter().accept(params.counterGetter().get() + 1);
        return true;
    }
}