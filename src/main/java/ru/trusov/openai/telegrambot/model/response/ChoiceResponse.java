package ru.trusov.openai.telegrambot.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.trusov.openai.telegrambot.model.common.Message;

import java.io.Serializable;

@Data
public class ChoiceResponse implements Serializable {
    private Integer index;
    private Message message;
    @JsonProperty("finish_reason")
    private String finishReason;
}