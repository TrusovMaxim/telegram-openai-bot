package ru.trusov.openai.telegrambot.telegram.notify;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.service.user.UserService;

@Component
@RequiredArgsConstructor
public class NotifyRunner implements CommandLineRunner {
    private final UserService userService;

    @Override
    public void run(String... args) {
        userService.notifyAllUsersAboutNewFeatures();
    }
}