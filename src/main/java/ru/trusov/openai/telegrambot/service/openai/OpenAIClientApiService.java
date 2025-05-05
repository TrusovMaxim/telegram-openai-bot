package ru.trusov.openai.telegrambot.service.openai;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.trusov.openai.telegrambot.config.openai.OpenAIClientConfiguration;
import ru.trusov.openai.telegrambot.model.request.ChatGPTRequest;
import ru.trusov.openai.telegrambot.model.request.GenerateImageRequest;
import ru.trusov.openai.telegrambot.model.request.WhisperTranslatorRequest;
import ru.trusov.openai.telegrambot.model.response.ChatGPTResponse;
import ru.trusov.openai.telegrambot.model.response.GenerateImageResponse;
import ru.trusov.openai.telegrambot.model.response.WhisperTranslatorResponse;

@FeignClient(
        name = "openai-service",
        url = "https://api.openai.com/v1",
        configuration = OpenAIClientConfiguration.class
)
public interface OpenAIClientApiService {

    @PostMapping(value = "/audio/transcriptions", headers = {"Content-Type=multipart/form-data"})
    WhisperTranslatorResponse createTranscription(@ModelAttribute WhisperTranslatorRequest whisperTranslatorRequest);

    @PostMapping(value = "/audio/translations", headers = {"Content-Type=multipart/form-data"})
    WhisperTranslatorResponse createTranslation(@ModelAttribute WhisperTranslatorRequest whisperTranslatorRequest);

    @PostMapping(value = "/images/generations")
    GenerateImageResponse generateImage(@RequestBody GenerateImageRequest generateImageRequest);

    @PostMapping(value = "/chat/completions", headers = {"Content-Type=application/json"})
    ChatGPTResponse chat(@RequestBody ChatGPTRequest chatGPTRequest);
}