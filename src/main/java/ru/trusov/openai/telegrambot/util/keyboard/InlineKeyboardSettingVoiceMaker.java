package ru.trusov.openai.telegrambot.util.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.trusov.openai.telegrambot.constant.BotOptions;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardSettingVoiceMaker {

    public InlineKeyboardMarkup getInlineMessageButtons() {
        var rowList = new ArrayList<List<InlineKeyboardButton>>();
        rowList.add(getButton(BotOptions.OPTION_VOICE_TRANSCRIPTION, BotOptions.OPTION_VOICE_TRANSCRIPTION));
        rowList.add(getButton(BotOptions.OPTION_VOICE_TRANSLATION, BotOptions.OPTION_VOICE_TRANSLATION));
        rowList.add(getButton(BotOptions.OPTION_BACK_TO_SETTINGS_MENU, BotOptions.OPTION_BACK_TO_SETTINGS_MENU));
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> getButton(String buttonName, String buttonCallBackData) {
        var button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallBackData);
        var keyboardButtonsRow = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow.add(button);
        return keyboardButtonsRow;
    }
}