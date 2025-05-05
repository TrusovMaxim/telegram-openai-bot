package ru.trusov.openai.telegrambot.model.enums;

import lombok.Getter;

@Getter
public enum ImageSizeEnum {
    MINIMUM_SIZE("256x256"),
    AVERAGE_SIZE("512x512"),
    BIG_SIZE("1024x1024");

    private final String value;

    ImageSizeEnum(String value) {
        this.value = value;
    }
}