package ru.trusov.openai.telegrambot.util.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardCommandMenuMaker {

    public InlineKeyboardMarkup getCommandMenu() {
        var rows = new ArrayList<List<InlineKeyboardButton>>();
        rows.add(List.of(
                button("💬 Чат", "/gpt"),
                button("♻️ Сброс чата", "/gpt_reset")
        ));
        rows.add(List.of(
                button("🎧 Перевод", "/translator"),
                button("▶️ YouTube", "/youtube")
        ));
        rows.add(List.of(
                button("🖼 Изображения", "/image"),
                button("💎 Купить токены", "/buy_images")
        ));
        rows.add(List.of(
                button("🧾 Баланс", "/balance"),
                button("💡 О боте", "/info")
        ));
        rows.add(List.of(
                button("✍️ Отзыв", "/feedback"),
                button("⚙️ Настройки", "/settings")
        ));
        rows.add(List.of(
                button("🌟 Премиум", "/buy_premium")
        ));
        return new InlineKeyboardMarkup(rows);
    }

    private InlineKeyboardButton button(String text, String callback) {
        var btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(callback);
        return btn;
    }
}