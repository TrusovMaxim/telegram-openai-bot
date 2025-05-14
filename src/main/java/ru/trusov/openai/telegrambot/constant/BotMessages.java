package ru.trusov.openai.telegrambot.constant;

public interface BotMessages {
    String MESSAGE_INFO_INTRO = """
            - Этот бот находится в тестовом режиме!
            - Этот бот не всегда онлайн!
            - Вы можете написать свой отзыв анонимно, используя команду «/feedback».
            \nВы автоматически переключились на chatGPT.
            """;
    String MESSAGE_COMMAND_LIST = """
            Вас приветствует умный ассистент!
            \nДоступны быстрые команды:
            •/gpt - Чат с GPT.
            •/gpt_reset - Сброс диалога.
            •/translator - Голос в текст.
            •/youtube - Краткий обзор YouTube-видео.
            •/setting_voice - Модель перевода голоса.
            •/image - Генерация изображений.
            •/setting_image - Размер изображения.
            •/commands - Список команд.
            •/info - О проекте.
            •/feedback - Обратная связь.
            \nДоступны команды через кнопки:
            «Об авторе» — Информация об авторе.
            «Донат» — Поддержать развитие проекта.
            \nВыберите подходящую команду из меню.
            """;
    String MESSAGE_ABOUT_AUTHOR = """
            Информация об авторе:
            👨‍💻 Разработчик — @Tr_Maxim
            💻 Язык — Java
            """;
    String MESSAGE_DONATE_INFO = """
            Поддержать развитие проекта:
            СБЕР: +7 (999) 775 70 50
            """;
    String MESSAGE_FEEDBACK_THANKS = "Спасибо за ваш отзыв!\n\nВы автоматически переключились на «chatGPT».";
}