package ru.trusov.openai.telegrambot.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonNameEnum {
    ABOUT("Об авторе"),
    DONATE("Донат");

    private final String buttonName;
}