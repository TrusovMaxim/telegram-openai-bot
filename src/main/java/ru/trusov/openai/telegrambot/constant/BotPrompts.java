package ru.trusov.openai.telegrambot.constant;

public interface BotPrompts {
    String PROMPT_FEEDBACK_WRITE = "Пожалуйста, напишите свой отзыв:";
    String PROMPT_VOICE_REQUIRED = "Вы должны отправить голосовое сообщение.";
    String PROMPT_IMAGE_DESCRIPTION_REQUEST = "Пожалуйста, отправьте описание для генерируемого изображения. Чем более подробное описание, тем больше вероятность, что вы получите результат, который хотите вы или ваш конечный пользователь.";
    String PROMPT_VOICE_SEND = "Пожалуйста, отправьте голосовое сообщение.";
}