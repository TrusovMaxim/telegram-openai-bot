package ru.trusov.openai.telegrambot.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonNameEnum {
    DONATE("💸 Донат", "donate"),
    SUPPORT("🛠 Поддержка", "support");

    private final String buttonName;
    private final String commandName;
}