package ru.trusov.openai.telegrambot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trusov.openai.telegrambot.model.request.ChatRequest;
import ru.trusov.openai.telegrambot.model.request.GenerateImageRequest;
import ru.trusov.openai.telegrambot.model.request.TranslatorRequest;
import ru.trusov.openai.telegrambot.model.response.ChatGPTResponse;
import ru.trusov.openai.telegrambot.model.response.GenerateImageResponse;
import ru.trusov.openai.telegrambot.model.response.WhisperTranslatorResponse;
import ru.trusov.openai.telegrambot.service.openai.impl.OpenAIClientApiServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class OpenAIClientController {
    private final OpenAIClientApiServiceImpl openAIClientApiServiceImpl;

    @PostMapping(value = "/transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WhisperTranslatorResponse> createTranscription(@ModelAttribute TranslatorRequest translatorRequest) {
        var response = openAIClientApiServiceImpl.speechToTextTranscriptions(translatorRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/translation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WhisperTranslatorResponse> createTranslation(@ModelAttribute TranslatorRequest translatorRequest) {
        var response = openAIClientApiServiceImpl.speechToTextTranslations(translatorRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/generate-images")
    public ResponseEntity<GenerateImageResponse> generateImage(@Valid @RequestBody GenerateImageRequest request) {
        var response = openAIClientApiServiceImpl.generateImage(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/chat/completions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatGPTResponse> chat(@RequestBody ChatRequest chatRequest) {
        var response = openAIClientApiServiceImpl.chat(chatRequest);
        return ResponseEntity.ok(response);
    }
}