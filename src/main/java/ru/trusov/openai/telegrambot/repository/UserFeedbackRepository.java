package ru.trusov.openai.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trusov.openai.telegrambot.model.entity.UserFeedback;

@Repository
public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Integer> {
}