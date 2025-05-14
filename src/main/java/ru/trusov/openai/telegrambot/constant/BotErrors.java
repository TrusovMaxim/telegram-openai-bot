package ru.trusov.openai.telegrambot.constant;

public interface BotErrors {
    String ERROR_UNSUPPORTED_MESSAGE = "Неподдерживаемый формат сообщения.";
    String ERROR_INCORRECT_COMMAND = "Вам нужно выбрать команду из меню.";
    String ERROR_VOICE_MESSAGE_TOO_LONG = "Ваше сообщение слишком длинное. Сообщение должно быть до 10 минут.";
    String ERROR_EMPTY_VOICE_MESSAGE = "Вы отправили пустое голосовое сообщение? Пожалуйста, попробуйте еще раз.";
    String ERROR_IMAGE_FORMAT_UNSUPPORTED = "Данный размер не поддерживается. Выберите подходящий размер для генерируемого изображения";
    String ERROR_INTERNAL_TRANSLATION = "Произошла ошибка при обработке голосового сообщения. Попробуйте позже.";
    String ERROR_YOUTUBE_SUBTITLE_NOT_FOUND = "Не удалось обработать видео. Возможно, у него нет доступных субтитров.";
    String ERROR_YOUTUBE_PROCESSING = "Ошибка при обработке видео с YouTube. Попробуйте другое видео или повторите позже.";
    String ERROR_YOUTUBE_INVALID_URL = "Пожалуйста, отправьте корректную ссылку на YouTube-видео.";
}