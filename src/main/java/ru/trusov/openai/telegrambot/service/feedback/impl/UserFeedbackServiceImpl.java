package ru.trusov.openai.telegrambot.service.feedback.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.entity.UserFeedback;
import ru.trusov.openai.telegrambot.repository.UserFeedbackRepository;
import ru.trusov.openai.telegrambot.service.feedback.UserFeedbackService;

import java.util.Date;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserFeedbackServiceImpl implements UserFeedbackService {
    private final UserFeedbackRepository userFeedbackRepository;

    @Override
    @Transactional
    public void saveFeedback(String feedback, User user) {
        var userFeedback = new UserFeedback();
        userFeedback.setFeedback(feedback);
        userFeedback.setUser(user);
        userFeedback.setCurrentTime(new Date());
        userFeedbackRepository.save(userFeedback);
    }
}