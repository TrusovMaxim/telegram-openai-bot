package ru.trusov.openai.telegrambot.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.request.GenerateImageRequest;
import ru.trusov.openai.telegrambot.model.response.GenerateImageResponse;
import ru.trusov.openai.telegrambot.service.openai.impl.OpenAIClientApiServiceImpl;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final OpenAIClientApiServiceImpl openAIClientApiService;

    public String generate(ImageSizeEnum size, String prompt) {
        try {
            var request = GenerateImageRequest.builder()
                    .model("dall-e-3")
                    .prompt(prompt)
                    .n(1)
                    .size(size.getValue())
                    .quality("hd")
                    .build();
            GenerateImageResponse response = null;
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    response = openAIClientApiService.generateImage(request);
                    break;
                } catch (HttpServerErrorException e) {
                    log.warn("Попытка {}: ошибка 500 от OpenAI при генерации изображения. Повтор через {} мс", attempt, attempt * 1000);
                    Thread.sleep(attempt * 1000);
                }
            }
            if (response == null || response.getData().isEmpty()) {
                log.warn("OpenAI вернул пустой ответ. Prompt: {}", prompt);
                throw new RuntimeException("Ошибка генерации изображения");
            }
            return response.getData().getFirst().getUrl();
        } catch (Exception e) {
            log.error("Ошибка при генерации изображения: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка генерации изображения");
        }
    }
}