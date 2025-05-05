package ru.trusov.openai.telegrambot.service.openai.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trusov.openai.telegrambot.model.common.Message;
import ru.trusov.openai.telegrambot.model.request.*;
import ru.trusov.openai.telegrambot.model.response.ChatGPTResponse;
import ru.trusov.openai.telegrambot.model.response.GenerateImageResponse;
import ru.trusov.openai.telegrambot.model.response.WhisperTranslatorResponse;
import ru.trusov.openai.telegrambot.service.openai.OpenAIClientApiService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OpenAIClientApiServiceImpl {
    private final OpenAIClientApiService openAIClientApiService;
    private final static String ROLE_USER = "user";
    @Value("${APP_OPEN_AI_AUDIO_MODEL}")
    private String whisperModel;
    @Value("${APP_OPEN_AI_MODEL}")
    private String gptModel;

    public WhisperTranslatorResponse speechToTextTranscriptions(TranslatorRequest translatorRequest) {
        var whisperTranscriptionRequest = WhisperTranslatorRequest.builder()
                .model(whisperModel)
                .file(translatorRequest.getFile())
                .build();
        return openAIClientApiService.createTranscription(whisperTranscriptionRequest);
    }

    public WhisperTranslatorResponse speechToTextTranslations(TranslatorRequest translatorRequest) {
        var whisperTranscriptionRequest = WhisperTranslatorRequest.builder()
                .model(whisperModel)
                .file(translatorRequest.getFile())
                .build();
        return openAIClientApiService.createTranslation(whisperTranscriptionRequest);
    }

    public GenerateImageResponse generateImage(GenerateImageRequest generateImageRequest) {
        return openAIClientApiService.generateImage(generateImageRequest);
    }

    public ChatGPTResponse chat(ChatRequest chatRequest) {
        var message = Message.builder()
                .role(ROLE_USER)
                .content(chatRequest.getQuestion())
                .build();
        var chatGPTRequest = ChatGPTRequest.builder()
                .model(gptModel)
                .messages(Collections.singletonList(message))
                .build();
        return openAIClientApiService.chat(chatGPTRequest);
    }
}