package ru.trusov.openai.telegrambot.service.feedback;

import ru.trusov.openai.telegrambot.model.entity.User;

public interface UserFeedbackService {
    void saveFeedback(String feedback, User user);
}