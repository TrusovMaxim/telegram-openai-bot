package ru.trusov.openai.telegrambot.config.telegram;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.trusov.openai.telegrambot.bot.TelegramBotFacade;

@Slf4j
@Component
@AllArgsConstructor
public class BotInitializer {
    private final TelegramBotFacade tgBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(tgBot);
            log.info("Telegram бот успешно зарегистрирован.");
        } catch (TelegramApiException e) {
            log.error("Не удалось зарегистрировать Telegram бота", e);
        }
    }
}