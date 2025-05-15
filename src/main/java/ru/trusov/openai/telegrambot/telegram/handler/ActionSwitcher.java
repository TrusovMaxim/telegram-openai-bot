package ru.trusov.openai.telegrambot.telegram.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.telegram.processor.*;

@Component
@AllArgsConstructor
public class ActionSwitcher {
    private final StartProcessor startProcessor;
    private final ChatGptProcessor chatGptProcessor;
    private final ImageProcessor imageProcessor;
    private final FeedbackProcessor feedbackProcessor;
    private final TranslatorProcessor translatorProcessor;
    private final YoutubeProcessor youtubeProcessor;

    public void route(User user, Long chatId, String text, UserActionPathEnum action) {
        switch (user.getBotStateEnum()) {
            case START -> startProcessor.process(user, chatId, action);
            case CHAT_GPT -> chatGptProcessor.process(user, chatId, text, action);
            case IMAGE -> imageProcessor.process(user, chatId, text, action);
            case FEEDBACK -> feedbackProcessor.process(user, chatId, text, action);
            case TRANSLATOR -> translatorProcessor.process(user, chatId, action);
            case YOUTUBE -> youtubeProcessor.process(user, chatId, text, action);
        }
    }
}