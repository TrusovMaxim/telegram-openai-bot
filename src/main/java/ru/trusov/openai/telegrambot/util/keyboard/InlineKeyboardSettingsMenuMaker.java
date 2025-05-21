package ru.trusov.openai.telegrambot.util.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.trusov.openai.telegrambot.constant.BotOptions;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardSettingsMenuMaker {

    public InlineKeyboardMarkup getInlineSettingsMenu() {
        var rowList = new ArrayList<List<InlineKeyboardButton>>();
        rowList.add(getButton(BotOptions.OPTION_VOICE_SETTINGS, BotOptions.OPTION_VOICE_SETTINGS));
        rowList.add(getButton(BotOptions.OPTION_IMAGE_SETTINGS, BotOptions.OPTION_IMAGE_SETTINGS));
        var markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rowList);
        return markup;
    }

    private List<InlineKeyboardButton> getButton(String name, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(callbackData);
        return List.of(button);
    }
}