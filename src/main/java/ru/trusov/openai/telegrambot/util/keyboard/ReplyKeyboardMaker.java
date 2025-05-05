package ru.trusov.openai.telegrambot.util.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.trusov.openai.telegrambot.model.enums.ButtonNameEnum;

import java.util.ArrayList;

public class ReplyKeyboardMaker {

    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        var row = new KeyboardRow();
        row.add(new KeyboardButton(ButtonNameEnum.DONATE.getButtonName()));
        row.add(new KeyboardButton(ButtonNameEnum.ABOUT.getButtonName()));
        var keyboard = new ArrayList<KeyboardRow>();
        keyboard.add(row);
        final var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }
}