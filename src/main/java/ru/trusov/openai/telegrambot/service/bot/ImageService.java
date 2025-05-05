package ru.trusov.openai.telegrambot.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.request.GenerateImageRequest;
import ru.trusov.openai.telegrambot.model.response.GenerateImageResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    @Value("${RESOURCE_OPEN_AI_URL_GENERATE_IMAGE}")
    private String generateImageUrl;

    public String generate(ImageSizeEnum size, String prompt) {
        try {
            var request = GenerateImageRequest.builder()
                    .n(1)
                    .size(size.getValue())
                    .prompt(prompt)
                    .build();
            var response = new RestTemplate().postForObject(
                    generateImageUrl,
                    request,
                    GenerateImageResponse.class
            );
            if (response == null || response.getData().isEmpty()) {
                log.warn("Сервис генерации изображений вернул пустой ответ. Prompt: {}", prompt);
                throw new RuntimeException("Ошибка генерации изображения");
            }
            return response.getData().getFirst().getUrl();
        } catch (Exception e) {
            log.error("Ошибка при генерации изображения: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка генерации изображения");
        }
    }
}