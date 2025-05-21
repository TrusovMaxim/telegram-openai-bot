package ru.trusov.openai.telegrambot.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserActionPathEnum {
    CHAT_GPT("/gpt"),
    RESET_GPT_DIALOG("/gpt_reset"),
    TRANSLATOR("/translator"),
    YOUTUBE("/youtube"),
    IMAGE("/image"),
    SETTINGS("/settings"),
    BUY_IMAGES("/buy_images"),
    BALANCE("/balance"),
    BUY_PREMIUM("/buy_premium"),
    INFO("/info"),
    FEEDBACK("/feedback"),
    COMMANDS("/commands"),
    SETTING_VOICE("/setting_voice"),
    SETTING_IMAGE("/setting_image"),
    DONATE("Донат"),
    ABOUT_AUTHOR("Об авторе");

    private final String command;

    public static UserActionPathEnum parse(String command) {
        return Arrays.stream(UserActionPathEnum.values())
                .filter(userActionPathEnum -> userActionPathEnum.command.equalsIgnoreCase(command))
                .findFirst()
                .orElse(null);
    }
}