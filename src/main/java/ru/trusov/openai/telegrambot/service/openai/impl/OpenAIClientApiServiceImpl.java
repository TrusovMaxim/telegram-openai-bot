package ru.trusov.openai.telegrambot.service.openai.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.trusov.openai.telegrambot.constant.BotPrompts;
import ru.trusov.openai.telegrambot.model.common.Message;
import ru.trusov.openai.telegrambot.model.request.*;
import ru.trusov.openai.telegrambot.model.response.ChatGPTResponse;
import ru.trusov.openai.telegrambot.model.response.GenerateImageResponse;
import ru.trusov.openai.telegrambot.model.response.WhisperTranslatorResponse;
import ru.trusov.openai.telegrambot.service.openai.OpenAIClientApiService;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIClientApiServiceImpl {
    private final OpenAIClientApiService openAIClientApiService;
    private final static String ROLE_USER = "user";
    @Value("${APP_OPEN_AI_AUDIO_MODEL}")
    private String whisperModel;
    @Value("${APP_OPEN_AI_MODEL}")
    private String gptModel;
    private static final int MAX_SUBTITLE_LENGTH = 30000;

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

    public String summarizeText(String subtitles) {
        var prompt = BotPrompts.PROMPT_SUMMARIZE_YOUTUBE;
        if (subtitles.length() > MAX_SUBTITLE_LENGTH) {
            log.warn("Субтитры слишком длинные: {} символов. Обрезаем до {}", subtitles.length(), MAX_SUBTITLE_LENGTH);
            subtitles = subtitles.substring(0, MAX_SUBTITLE_LENGTH);
        }
        var fullPrompt = prompt + "\n\n" + subtitles;
        var chatRequest = ChatRequest.builder()
                .question(fullPrompt)
                .build();
        try {
            var response = chat(chatRequest);
            if (response == null || response.getChoiceResponses() == null || response.getChoiceResponses().isEmpty()) {
                log.warn("OpenAI не вернул ответ на запрос summarizeText");
                throw new RuntimeException("Ошибка получения ответа от OpenAI");
            }
            return response.getChoiceResponses().getFirst().getMessage().getContent().trim();
        } catch (Exception e) {
            log.error("Ошибка при генерации краткого обзора: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось получить краткий обзор видео");
        }
    }
}