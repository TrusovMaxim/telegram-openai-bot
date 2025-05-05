package ru.trusov.openai.telegrambot.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.trusov.openai.telegrambot.model.common.Message;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ChatGPTRequest implements Serializable {
    private String model;
    private List<Message> messages;
}