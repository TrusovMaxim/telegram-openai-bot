package ru.trusov.openai.telegrambot.telegram.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserService;

import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
public class BuyImageProcessor {
    private final UserService userService;
    private final MessageSenderService messageSenderService;

    public void process(User user, Long chatId) {
        var amount = 10;
        user.setImageBalance((user.getImageBalance() == null ? 0 : user.getImageBalance()) + amount);
        userService.save(user);
        messageSenderService.send(MessageFormat.format(BotMessages.MESSAGE_IMAGE_BALANCE_TOPPED_UP, user.getImageBalance()), chatId);
    }
}