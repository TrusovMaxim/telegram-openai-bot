package ru.trusov.openai.telegrambot.constant;

public interface BotSectionState {
    String STATE_CHAT_ALREADY_ACTIVATED = "⚠️ Чат уже активирован. Пожалуйста, напишите запрос.";
    String STATE_CHAT_ALREADY_IN_SECTION = "ℹ️ Вы уже находитесь в этом разделе бота.";
    String STATE_CHAT_SWITCHED_TO_GPT = """
            💬 Вы перешли в режим ChatGPT.
            
            🆓 Бесплатно: до 20 сообщений в день, без истории.
            💎 Премиум: диалог с историей (до 15 сообщений), автоматическое запоминание.
            
            🔁 Сбросить текущий диалог: /gpt_reset
            """;
    String STATE_CHAT_SWITCHED_TO_YOUTUBE = "🎬 Режим YouTube активирован. Пришлите ссылку на видео — я сделаю краткое резюме. Обрабатываются первые ~15 минут.";
    String STATE_CHAT_GPT_DIALOG_RESET = "♻️ Диалог с GPT был сброшен.\n\n";
    String STATE_REQUEST_SENT = "⏳ Запрос отправлен. Ожидайте ответа...";
    String STATE_REQUEST_IMAGE_SENT = "🖼 Генерирую изображение... Пожалуйста, подождите.";
    String STATE_YOUTUBE_SUBTITLE_LOADING = "📥 Загружаю субтитры с YouTube...";
    String STATE_YOUTUBE_SUMMARIZING = "🧠 Формирую краткое содержание видео...";
    String STATE_CHOICE_TRANSLATION_PROMPT = """
            🎧 Выберите режим голосового перевода:
            
            ▪️ Transcription — расшифровка аудио (на исходном языке)
            ▪️ Translation — перевод на английский язык
            
            ⚙️ Настройки можно изменить через /setting_voice
            """;
    String STATE_CHOICE_IMAGE_SIZE_PROMPT = """
            🖼 Выберите размер изображения:
            
            ▪️ 256x256 — минимальный (быстрое создание)
            ▪️ 512x512 — средний
            ▪️ 1024x1024 — большой (дольше генерируется)
            
            ⚙️ Изменить размер позже: /setting_image
            """;
    String STATE_CHOICE_TRANSCRIPTION_RESPONSE = "📝 Вы выбрали: transcription.\nТеперь можно отправить голосовое сообщение.\n\nВы автоматически перешли в режим «translator».";
    String STATE_CHOICE_TRANSLATION_RESPONSE = "🌐 Вы выбрали: translation.\nТеперь можно отправить голосовое сообщение.\n\nВы автоматически перешли в режим «translator».";
    String STATE_CHOICE_IMAGE_SIZE_SMALL = "✅ Размер выбран: 256x256.\nПожалуйста, отправьте описание для генерации изображения.\n\nВы перешли в режим /image.";
    String STATE_CHOICE_IMAGE_SIZE_MEDIUM = "✅ Размер выбран: 512x512.\nПожалуйста, отправьте описание для генерации изображения.\n\nВы перешли в режим /image.";
    String STATE_CHOICE_IMAGE_SIZE_LARGE = "✅ Размер выбран: 1024x1024.\nПожалуйста, отправьте описание для генерации изображения.\n\nВы перешли в режим /image.";
}