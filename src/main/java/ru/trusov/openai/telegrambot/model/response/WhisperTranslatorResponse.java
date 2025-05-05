package ru.trusov.openai.telegrambot.model.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class WhisperTranslatorResponse implements Serializable {
    private String text;
}