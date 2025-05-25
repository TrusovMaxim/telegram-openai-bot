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
                button("📄 Файлы", "/file_summarize")
        ));
        rows.add(List.of(
                button("💎 Купить токены", "/buy_images"),
                button("🧾 Баланс", "/balance")
        ));
        rows.add(List.of(
                button("💡 О боте", "/info"),
                button("✍️ Отзыв", "/feedback")
        ));
        rows.add(List.of(
                button("⚙️ Настройки", "/settings"),
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