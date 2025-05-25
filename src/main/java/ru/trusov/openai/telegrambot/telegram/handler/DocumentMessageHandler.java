package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.telegram.processor.PdfProcessor;

@Component
@AllArgsConstructor
public class DocumentMessageHandler {
    private final PdfProcessor pdfProcessor;

    public void handle(Update update, User user) {
        var doc = update.getMessage().getDocument();
        var chatId = update.getMessage().getChatId();
        if (user.getBotStateEnum() == BotStateEnum.FILE_SUMMARIZE) {
            pdfProcessor.handleFile(user, chatId, doc);
        }
    }
}