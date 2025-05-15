package ru.trusov.openai.telegrambot.constant;

public interface BotErrors {
    String ERROR_UNSUPPORTED_MESSAGE = "⚠️ Неподдерживаемый формат сообщения.";
    String ERROR_INCORRECT_COMMAND = "❗ Пожалуйста, выберите команду из меню.";
    String ERROR_VOICE_MESSAGE_TOO_LONG = "📢 Сообщение слишком длинное. Максимальная длина — 10 минут.";
    String ERROR_EMPTY_VOICE_MESSAGE = "🔇 Вы отправили пустое голосовое сообщение. Попробуйте ещё раз.";
    String ERROR_IMAGE_FORMAT_UNSUPPORTED = "🖼 Выбран неподдерживаемый размер изображения. Пожалуйста, выберите другой.";
    String ERROR_INTERNAL_TRANSLATION = "❌ Ошибка при обработке голосового сообщения. Попробуйте позже.";
    String ERROR_YOUTUBE_SUBTITLE_NOT_FOUND = "🎬 Не удалось обработать видео. Вероятно, у него нет субтитров.";
    String ERROR_YOUTUBE_PROCESSING = "⚠️ Ошибка при обработке YouTube-видео. Попробуйте другое или повторите позже.";
    String ERROR_YOUTUBE_INVALID_URL = "🔗 Пожалуйста, отправьте корректную ссылку на YouTube-видео.";
    String ERROR_PAYMENT_FAILED = "❌ Не удалось отправить платёж. Попробуйте позже.";
}