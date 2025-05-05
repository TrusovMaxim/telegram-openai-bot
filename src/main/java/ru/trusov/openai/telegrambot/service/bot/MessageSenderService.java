package ru.trusov.openai.telegrambot.service.bot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.constant.BotSectionState;
import ru.trusov.openai.telegrambot.constant.BotTemplates;
import ru.trusov.openai.telegrambot.util.keyboard.InlineKeyboardSettingImageMaker;
import ru.trusov.openai.telegrambot.util.keyboard.InlineKeyboardSettingVoiceMaker;
import ru.trusov.openai.telegrambot.util.keyboard.ReplyKeyboardMaker;

import java.text.MessageFormat;

@Slf4j
@Setter
@Service
public class MessageSenderService {
    private TelegramLongPollingBot telegramBot;

    public void send(String text, Long chatId) {
        try {
            var message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить сообщение", e);
        }
    }

    public void edit(String text, Long chatId, Integer messageId) {
        try {
            var message = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(text)
                    .build();
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при редактировании сообщения в чате {}, messageId {}: {}", chatId, messageId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отредактировать сообщение", e);
        }
    }

    public void sendCommandList(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(BotMessages.MESSAGE_COMMAND_LIST)
                .replyMarkup(new ReplyKeyboardMaker().getMainMenuKeyboard())
                .build();
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке списка команд в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить список команд", e);
        }
    }

    public void sendImagePrompt(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(BotSectionState.STATE_CHOICE_IMAGE_SIZE_PROMPT)
                .replyMarkup(new InlineKeyboardSettingImageMaker().getInlineMessageButtons())
                .build();
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке запроса генерации изображения в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить сообщение с выбором размера изображения", e);
        }
    }

    public void sendImageLink(String url, Long chatId) {
        send(MessageFormat.format(BotTemplates.TEMPLATE_IMAGE_URL_RESULT, url), chatId);
    }

    public void sendTranslatorPrompt(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(BotSectionState.STATE_CHOICE_TRANSLATION_PROMPT)
                .replyMarkup(new InlineKeyboardSettingVoiceMaker().getInlineMessageButtons())
                .build();
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения с выбором типа перевода в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить сообщение с выбором типа перевода", e);
        }
    }
}