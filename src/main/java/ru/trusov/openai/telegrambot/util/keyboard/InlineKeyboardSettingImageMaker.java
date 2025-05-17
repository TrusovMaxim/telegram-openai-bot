package ru.trusov.openai.telegrambot.util.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.trusov.openai.telegrambot.constant.BotOptions;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardSettingImageMaker {

    public InlineKeyboardMarkup getInlineMessageButtons() {
        var rowList = new ArrayList<List<InlineKeyboardButton>>();
        rowList.add(getButton(BotOptions.OPTION_IMAGE_SIZE_SQUARE, BotOptions.OPTION_IMAGE_SIZE_SQUARE));
        rowList.add(getButton(BotOptions.OPTION_IMAGE_SIZE_VERTICAL, BotOptions.OPTION_IMAGE_SIZE_VERTICAL));
        rowList.add(getButton(BotOptions.OPTION_IMAGE_SIZE_HORIZONTAL, BotOptions.OPTION_IMAGE_SIZE_HORIZONTAL));
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