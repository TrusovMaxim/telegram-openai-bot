package ru.trusov.openai.telegrambot.bot;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.telegram.handler.CallbackQueryHandler;
import ru.trusov.openai.telegrambot.telegram.handler.DocumentMessageHandler;
import ru.trusov.openai.telegrambot.telegram.handler.TextMessageHandler;
import ru.trusov.openai.telegrambot.telegram.handler.VoiceMessageHandler;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class TelegramBotFacade extends TelegramLongPollingBot {
    private final TextMessageHandler textMessageHandler;
    private final VoiceMessageHandler voiceMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final UserService userService;
    private final String appTelegramBotUsername;
    private final String appTelegramBotToken;
    private final MessageSenderService messageSenderService;
    private final DocumentMessageHandler documentMessageHandler;

    @PostConstruct
    public void init() {
        messageSenderService.setTelegramBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasPreCheckoutQuery()) {
            var queryId = update.getPreCheckoutQuery().getId();
            try {
                this.execute(new AnswerPreCheckoutQuery(queryId, true));
            } catch (Exception e) {
                log.error("Ошибка при подтверждении preCheckout: {}", e.getMessage(), e);
            }
            return;
        }
        if (update.hasMessage() && update.getMessage().hasSuccessfulPayment()) {
            var chatId = update.getMessage().getChatId();
            var user = userService.getUser(chatId);
            var payload = update.getMessage().getSuccessfulPayment().getInvoicePayload();
            if (user != null) {
                switch (payload) {
                    case "premium-month" -> {
                        user.setIsPremium(true);
                        user.setPremiumStart(LocalDateTime.now());
                        user.setPremiumEnd(LocalDateTime.now().plusMonths(1));
                        userService.save(user);
                        messageSenderService.send(BotMessages.MESSAGE_PREMIUM_ACTIVATED, chatId);
                    }
                    case "image-tokens-5" -> {
                        var current = user.getImageBalance() == null ? 0 : user.getImageBalance();
                        user.setImageBalance(current + 5);
                        userService.save(user);
                        messageSenderService.send(
                                MessageFormat.format(BotMessages.MESSAGE_IMAGE_BALANCE_TOPPED_UP, user.getImageBalance()),
                                chatId
                        );
                    }
                    default -> log.warn("Неизвестный payload оплаты: {}", payload);
                }
            }
            return;
        }
        if (update.hasMessage()) {
            var chatId = update.getMessage().getChatId();
            var user = userService.getUser(chatId);
            if (update.getMessage().hasText()) {
                textMessageHandler.handle(update, user);
            } else if (update.getMessage().hasVoice()) {
                voiceMessageHandler.handle(update, user);
            } else if (update.getMessage().hasVideoNote()) {
                voiceMessageHandler.handleVideoNote(update, user);
            } else if (update.getMessage().hasDocument()) {
                var userState = user.getBotStateEnum();
                if (userState == BotStateEnum.FILE_SUMMARIZE) {
                    documentMessageHandler.handle(update, user);
                } else {
                    textMessageHandler.sendUnsupported(chatId);
                }
            } else {
                textMessageHandler.sendUnsupported(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            callbackQueryHandler.handle(update);
        }
    }

    @Override
    public String getBotUsername() {
        return appTelegramBotUsername;
    }

    @Override
    public String getBotToken() {
        return appTelegramBotToken;
    }
}