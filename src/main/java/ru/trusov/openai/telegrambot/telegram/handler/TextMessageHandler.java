package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserService;

@Component
@AllArgsConstructor
public class TextMessageHandler {
    private final ActionSwitcher actionSwitcher;
    private final UserService userService;
    private final MessageSenderService messageSenderService;

    public void handle(Update update, User user) {
        var chatId = update.getMessage().getChatId();
        var enteredText = update.getMessage().getText();
        var action = UserActionPathEnum.parse(enteredText);
        if (user == null) {
            var from = update.getMessage().getFrom();
            userService.registerUser(from.getUserName(), from.getFirstName(), from.getLastName(), chatId);
            messageSenderService.sendCommandList(chatId);
        } else {
            actionSwitcher.route(user, chatId, enteredText, action);
        }
    }

    public void sendUnsupported(Long chatId) {
        messageSenderService.send(BotErrors.ERROR_UNSUPPORTED_MESSAGE, chatId);
    }
}