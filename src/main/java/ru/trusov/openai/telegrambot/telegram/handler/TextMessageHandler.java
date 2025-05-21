package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserService;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@AllArgsConstructor
public class TextMessageHandler {
    private final ActionSwitcher actionSwitcher;
    private final UserService userService;
    private final MessageSenderService messageSenderService;

    public void handle(Update update, User user) {
        var chatId = update.getMessage().getChatId();
        var text = update.getMessage().getText();
        if ("/buy_premium".equalsIgnoreCase(text)) {
            if (user != null && Boolean.TRUE.equals(user.getIsPremium())
                    && user.getPremiumEnd() != null
                    && LocalDateTime.now().isBefore(user.getPremiumEnd())) {
                var until = user.getPremiumEnd().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("ru")));
                messageSenderService.send(MessageFormat.format(BotMessages.MESSAGE_PREMIUM_ALREADY_ACTIVE, until), chatId);
            } else {
                messageSenderService.sendPremiumInvoice(chatId);
            }
            return;
        }
        if ("/buy_images".equalsIgnoreCase(text)) {
            if (user != null && user.getImageBalance() != null && user.getImageBalance() > 0) {
                messageSenderService.send(
                        MessageFormat.format(BotMessages.MESSAGE_IMAGE_BALANCE_WARNING, user.getImageBalance()),
                        chatId
                );
            }
            messageSenderService.sendImageInvoice(chatId);
            return;
        }
        if ("/commands".equalsIgnoreCase(text)) {
            messageSenderService.sendCommandMenu(chatId);
            return;
        }
        var action = UserActionPathEnum.parse(text);
        if (user == null) {
            var from = update.getMessage().getFrom();
            userService.registerUser(from.getUserName(), from.getFirstName(), from.getLastName(), chatId);
            messageSenderService.sendCommandList(chatId);
        } else {
            if (action == UserActionPathEnum.SETTINGS) {
                messageSenderService.sendSettingsMenu(chatId);
                return;
            }
            actionSwitcher.route(user, chatId, text, action);
        }
    }

    public void sendUnsupported(Long chatId) {
        messageSenderService.send(BotErrors.ERROR_UNSUPPORTED_MESSAGE, chatId);
    }
}