package ru.trusov.openai.telegrambot.constant;

public interface BotMessages {
    String MESSAGE_INFO_INTRO = """
            🤖 AIntellio Bot — умный Telegram-ассистент на базе OpenAI
            Работает в России без VPN 🇷🇺
            
            🆓 Бесплатно:
            • 💬 /gpt — Чат с GPT (до 20 сообщений в день, без истории)
            • 🎤 /translator — Перевод голосовых (до 10 в день)
            • 📺 /youtube — Обзор YouTube-видео (1 в день)
            • 🖼 /image — Генерация изображений (при наличии токенов)
            • 📨 /feedback — Отправка отзыва
            
            💎 Премиум-доступ (299₽ в месяц):
            • 💬 GPT с историей (до 15 сообщений)
            • 🎤 Безлимитный перевод голосовых
            • 📺 Безлимитная обработка видео
            • 🖼 Покупка токенов на изображения
            
            🛍 Получить премиум: /activate_premium
            💰 Купить токены: /buy_images
            📊 Баланс токенов: /balance
            
            🙏 Спасибо, что выбрали AIntellio!
            🔄 Вы переключены в режим ChatGPT.
            """;
    String MESSAGE_COMMAND_LIST = """
            Доступны команды:
            • /gpt — Чат с GPT (до 15 сообщений в истории)
            • /gpt_reset — Сброс диалога GPT
            • /translator — Расшифровка и перевод голосовых сообщений
            • /youtube — Краткий обзор YouTube-видео (до 15 минут)
            • /image — Генерация изображений (1 изображение = 1 токен)
            • /buy_images — Пополнить баланс: 10 генераций за 99₽
            • /balance — Сколько генераций изображений у вас осталось
            • /setting_voice — Настроить режим голосового перевода
            • /setting_image — Настроить размер изображений
            • /info — О боте, функциях и премиуме
            • /activate_premium - Активация премиума
            • /feedback — Отправить отзыв
            • /commands — Показать список команд
            """;
    String MESSAGE_ABOUT_AUTHOR = """
            👨‍💻 Автор бота: @Trusov_Maxim
            💻 Написано на: Java ☕
            """;
    String MESSAGE_DONATE_INFO = """
            🙌 Поддержать развитие проекта:
            💳 СБЕР: +7 (999) 775 70 50
            """;
    String MESSAGE_FEEDBACK_THANKS = "🙏 Спасибо за ваш отзыв!\n\n🔄 Вы переключены на режим «chatGPT».";
    String MESSAGE_NO_IMAGE_BALANCE = "🛑 У вас закончились генерации изображений. Пополните баланс через /buy_images";
    String MESSAGE_IMAGE_BALANCE_TOPPED_UP = "✅ Баланс пополнен! Вам доступно {0} генераций изображений.";
    String MESSAGE_IMAGE_BALANCE_CURRENT = "📊 У вас {0} генераций изображений.";
    String MESSAGE_PREMIUM_ACTIVATED = "🎉 Премиум-доступ активирован! Спасибо за поддержку 🙌";
}