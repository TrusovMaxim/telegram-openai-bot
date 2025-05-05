package ru.trusov.openai.telegrambot.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.model.enums.TranslatorTypeEnum;
import ru.trusov.openai.telegrambot.model.response.WhisperTranslatorResponse;
import ru.trusov.openai.telegrambot.util.file.DownloadFileVoice;
import ru.trusov.openai.telegrambot.util.file.GetUrlVoice;

import java.nio.file.Files;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslatorService {
    @Value("${RESOURCE_OPEN_AI_URL_TRANSLATION}")
    private String translationUrl;
    @Value("${RESOURCE_OPEN_AI_URL_TRANSCRIPTION}")
    private String transcriptionUrl;

    public String translate(TranslatorTypeEnum type, String fileId, Long chatId) {
        var resourceUrl = switch (type) {
            case TRANSLATION -> translationUrl;
            case TRANSCRIPTION -> transcriptionUrl;
        };
        try {
            var fileUrl = GetUrlVoice.getFileUrl(fileId);
            var fsr = new DownloadFileVoice().downloadAsResource(fileUrl, chatId);
            var map = new LinkedMultiValueMap<String, Object>();
            map.add("file", fsr);
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            var entity = new HttpEntity<>(map, headers);
            var response = new RestTemplate().postForObject(resourceUrl, entity, WhisperTranslatorResponse.class);
            Files.deleteIfExists(fsr.getFile().toPath());
            if (response == null || response.getText().isEmpty()) {
                log.warn("Пустой ответ от OpenAI. type={}, fileId={}, chatId={}", type, fileId, chatId);
                return BotErrors.ERROR_EMPTY_VOICE_MESSAGE;
            }
            return response.getText();
        } catch (Exception e) {
            log.error("Ошибка при переводе голосового сообщения. type={}, fileId={}, chatId={}", type, fileId, chatId, e);
            return BotErrors.ERROR_INTERNAL_TRANSLATION;
        }
    }
}