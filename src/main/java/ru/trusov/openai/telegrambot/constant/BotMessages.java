package ru.trusov.openai.telegrambot.constant;

public interface BotMessages {
    String MESSAGE_INFO_INTRO = """
            🤖 AIntellio Bot — умный Telegram-ассистент на базе OpenAI
            Работает в России без VPN 🇷🇺
            
            🆓 Бесплатно:
            • 💬 /gpt — Чат с GPT (до 20 сообщений в день, без истории)
            • 🎤 /translator — Перевод голосовых сообщений (до 10 в день)
            • 📺 /youtube — Обработка YouTube-видео (1 видео в день)
            • 🖼 /image — Генерация изображений (при наличии токенов)
            • 📨 /feedback — Отправка отзыва
            
            💎 Премиум-доступ — 299₽ в месяц:
            • 💬 GPT с историей (до 15 сообщений в диалоге)
            • 🎤 Безлимитный перевод голосовых сообщений
            • 📺 Безлимитная обработка видео
            • 🖼 Возможность покупки токенов для изображений
            
            🛍 Оформить премиум: /buy_premium
            💰 Купить токены: /buy_images
            📊 Баланс токенов: /balance
            
            🙏 Спасибо, что выбрали AIntellio!
            """;
    String MESSAGE_ABOUT_AUTHOR = """
            👨‍💻 Автор бота: @Trusov_Maxim
            💻 Написано на: Java ☕
            """;
    String MESSAGE_DONATE_INFO = """
            🙌 Поддержать развитие проекта:
            💳 СБЕР: +7 (999) 775 70 50
            """;
    String MESSAGE_FEEDBACK_THANKS = "🙏 Спасибо за ваш отзыв!\n\n🔄 Вы переключены на режим /gpt.";
    String MESSAGE_NO_IMAGE_BALANCE = "🛑 У вас закончились генерации изображений. Пополните баланс через /buy_images";
    String MESSAGE_IMAGE_BALANCE_TOPPED_UP = "✅ Баланс пополнен! Вам доступно {0} генераций изображений.";
    String MESSAGE_IMAGE_BALANCE_CURRENT = "📊 У вас {0} генераций изображений.";
    String MESSAGE_PREMIUM_ACTIVATED = "🎉 Премиум-доступ активирован! Спасибо за поддержку 🙌";
    String MESSAGE_PREMIUM_ALREADY_ACTIVE = "🟢 У вас уже активен премиум-доступ до {0}.";
    String MESSAGE_IMAGE_BALANCE_WARNING = "🟢 У вас уже есть {0} генераций изображений. Покупка увеличит ваш баланс.";
}