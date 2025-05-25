package ru.trusov.openai.telegrambot.util.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class InlineKeyboardInfoMenuMaker {

    public InlineKeyboardMarkup getInfoKeyboard() {
        var rows = List.of(
                List.of(
                        button("💬 GPT", "/gpt"),
                        button("🎧 Перевод", "/translator")
                ),
                List.of(
                        button("🖼 Изображения", "/image"),
                        button("▶️ YouTube", "/youtube")
                ),
                List.of(
                        button("📄 Файлы", "/file_summarize"),
                        button("📜 Команды", "/commands")
                ),
                List.of(
                        button("⚙️ Настройки", "/settings"),
                        button("🌟 Премиум", "/buy_premium")
                )
        );
        return new InlineKeyboardMarkup(rows);
    }

    private InlineKeyboardButton button(String text, String data) {
        return InlineKeyboardButton.builder().text(text).callbackData(data).build();
    }
}