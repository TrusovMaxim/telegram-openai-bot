package ru.trusov.openai.telegrambot.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserActionPathEnum {
    TRANSLATOR("/translator"),
    CHAT_GPT("/gpt"),
    RESET_GPT_DIALOG("/gpt_reset"),
    INFO("/info"),
    FEEDBACK("/feedback"),
    SETTING_VOICE("/setting_voice"),
    COMMANDS("/commands"),
    IMAGE("/image"),
    SETTING_IMAGE("/setting_image"),
    YOUTUBE("/youtube"),
    BALANCE("/balance"),
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