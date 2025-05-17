package ru.trusov.openai.telegrambot.model.enums;

import lombok.Getter;

@Getter
public enum ImageSizeEnum {
    SQUARE("1024x1024"),
    VERTICAL("1024x1792"),
    HORIZONTAL("1792x1024");

    private final String value;

    ImageSizeEnum(String value) {
        this.value = value;
    }
}