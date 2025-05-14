package ru.trusov.openai.telegrambot.model.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ChatRequest implements Serializable {
    private String question;
}