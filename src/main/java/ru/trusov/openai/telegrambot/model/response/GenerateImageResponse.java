package ru.trusov.openai.telegrambot.model.response;

import lombok.Data;

import java.util.List;

@Data
public class GenerateImageResponse {
    private List<GeneratedImageResponse> data;
}