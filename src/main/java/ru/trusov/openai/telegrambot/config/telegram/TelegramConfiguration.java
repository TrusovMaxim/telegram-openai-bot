package ru.trusov.openai.telegrambot.config.telegram;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfiguration {
    @Bean
    public String appTelegramBotUsername(@Value("${APP_TELEGRAM_BOT_USERNAME}") String username) {
        return username;
    }

    @Bean
    public String appTelegramBotToken(@Value("${APP_TELEGRAM_BOT_TOKEN}") String token) {
        return token;
    }
}