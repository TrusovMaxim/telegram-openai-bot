package ru.trusov.openai.telegrambot.service.bot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.constant.BotMessages;
import ru.trusov.openai.telegrambot.constant.BotSectionState;
import ru.trusov.openai.telegrambot.constant.BotTemplates;
import ru.trusov.openai.telegrambot.util.keyboard.*;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Setter
@Service
public class MessageSenderService {
    private TelegramLongPollingBot telegramBot;
    @Value("${telegram.payment.provider-token}")
    private String providerToken;

    public void send(String text, Long chatId) {
        try {
            var message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            message.enableHtml(true);
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
            message.enableHtml(true);
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при редактировании сообщения в чате {}, messageId {}: {}", chatId, messageId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отредактировать сообщение", e);
        }
    }

    public void sendWelcomeWithMenu(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(BotMessages.MESSAGE_INFO_INTRO)
                .replyMarkup(new ReplyKeyboardMaker().getMainMenuKeyboard())
                .build();
        message.enableHtml(true);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке приветственного сообщения в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить приветственное сообщение", e);
        }
    }

    public void updateMainMenuButtons(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text("⚙️ Меню обновлено — выберите нужный раздел ниже:")
                .replyMarkup(new ReplyKeyboardMaker().getMainMenuKeyboard())
                .build();
        message.enableHtml(true);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при обновлении клавиатуры в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить клавиатуру", e);
        }
    }

    public void sendImagePrompt(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(BotSectionState.STATE_CHOICE_IMAGE_SIZE_PROMPT)
                .replyMarkup(new InlineKeyboardSettingImageMaker().getInlineMessageButtons())
                .build();
        message.enableHtml(true);
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
        message.enableHtml(true);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения с выбором типа перевода в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить сообщение с выбором типа перевода", e);
        }
    }

    public void sendPremiumInvoice(Long chatId) {
        var prices = List.of(new LabeledPrice("Премиум-доступ на 1 месяц", 399_00));
        var invoice = new SendInvoice();
        invoice.setChatId(chatId);
        invoice.setTitle("Премиум-доступ");
        invoice.setDescription("Доступ ко всем функциям бота без ограничений на 30 дней.");
        invoice.setPayload("premium-month");
        invoice.setProviderToken(providerToken);
        invoice.setCurrency("RUB");
        invoice.setPrices(prices);
        invoice.setStartParameter("premium");
        try {
            telegramBot.execute(invoice);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке инвойса на премиум: {}", e.getMessage(), e);
            send(BotErrors.ERROR_PAYMENT_FAILED, chatId);
        }
    }

    public void sendImageInvoice(Long chatId) {
        var prices = List.of(new LabeledPrice("5 генераций изображений", 199_00));
        var invoice = new SendInvoice();
        invoice.setChatId(chatId);
        invoice.setTitle("Токены для изображений");
        invoice.setDescription("5 генераций изображений (1 изображение = 1 токен)");
        invoice.setPayload("image-tokens-5");
        invoice.setProviderToken(providerToken);
        invoice.setCurrency("RUB");
        invoice.setPrices(prices);
        invoice.setStartParameter("buy_images");
        try {
            telegramBot.execute(invoice);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке инвойса на изображения: {}", e.getMessage(), e);
            send(BotErrors.ERROR_IMAGE_PAYMENT_FAILED, chatId);
        }
    }

    public void editVoiceSettings(Long chatId, Integer messageId) {
        var message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(BotSectionState.STATE_CHOICE_TRANSLATION_PROMPT)
                .replyMarkup(new InlineKeyboardSettingVoiceMaker().getInlineMessageButtons())
                .build();
        message.enableHtml(true);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при редактировании сообщения (настройки перевода) в чат {}: {}", chatId, e.getMessage(), e);
        }
    }

    public void editImageSettings(Long chatId, Integer messageId) {
        var message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(BotSectionState.STATE_CHOICE_IMAGE_SIZE_PROMPT)
                .replyMarkup(new InlineKeyboardSettingImageMaker().getInlineMessageButtons())
                .build();
        message.enableHtml(true);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при редактировании сообщения (настройки изображения) в чат {}: {}", chatId, e.getMessage(), e);
        }
    }

    public void sendSettingsMenu(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(BotSectionState.STATE_SETTINGS_PROMPT)
                .replyMarkup(new InlineKeyboardSettingsMenuMaker().getInlineSettingsMenu())
                .build();
        message.enableHtml(true);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке меню настроек в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить меню настроек", e);
        }
    }

    public void editSettingsMenu(Long chatId, Integer messageId) {
        var message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(BotSectionState.STATE_SETTINGS_PROMPT)
                .replyMarkup(new InlineKeyboardSettingsMenuMaker().getInlineSettingsMenu())
                .build();
        message.enableHtml(true);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при редактировании меню настроек в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось изменить меню настроек", e);
        }
    }

    public void sendCommandMenu(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text("📜 Доступные команды:")
                .replyMarkup(new InlineKeyboardCommandMenuMaker().getCommandMenu())
                .build();
        message.enableHtml(true);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке меню команд в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить меню команд", e);
        }
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        try {
            telegramBot.execute(new DeleteMessage(chatId.toString(), messageId));
        } catch (TelegramApiException e) {
            log.error("Ошибка при удалении сообщения в чате {}: {}", chatId, e.getMessage(), e);
        }
    }

    public void sendInfoWithButtons(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(BotMessages.MESSAGE_INFO)
                .replyMarkup(new InlineKeyboardInfoMenuMaker().getInfoKeyboard())
                .build();
        message.enableHtml(true);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке информации /info в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить информацию", e);
        }
    }
}