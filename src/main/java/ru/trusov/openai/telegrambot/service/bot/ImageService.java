package ru.trusov.openai.telegrambot.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.request.GenerateImageRequest;
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
            var response = openAIClientApiService.generateImage(request);
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