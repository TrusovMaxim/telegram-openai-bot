package ru.trusov.openai.telegrambot.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.model.enums.TranslatorTypeEnum;
import ru.trusov.openai.telegrambot.model.response.WhisperTranslatorResponse;
import ru.trusov.openai.telegrambot.util.file.DownloadFileVideoNote;
import ru.trusov.openai.telegrambot.util.file.DownloadFileVoice;
import ru.trusov.openai.telegrambot.util.file.GetUrlVoice;

import java.io.IOException;
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
        FileSystemResource fsr = null;
        try {
            var fileUrl = GetUrlVoice.getFileUrl(fileId);
            for (int attempt = 1; attempt <= 3; attempt++) {
                fsr = new FileSystemResource(new DownloadFileVoice().download(fileUrl, chatId));
                if (fsr.getFile().exists() && fsr.getFile().length() > 0) break;
                log.warn("Попытка {}: файл ещё не готов, ожидаю...", attempt);
                Thread.sleep(1000L * attempt);
            }
            if (!fsr.getFile().exists()) {
                log.error("Файл голосового сообщения не найден или пуст. chatId={}, fileId={}", chatId, fileId);
                return BotErrors.ERROR_EMPTY_VOICE_MESSAGE;
            }
            var entity = buildMultipartEntity(fsr);
            var response = sendRequestToWhisper(resourceUrl, entity, "voice");
            if (response == null || response.getText().isEmpty()) {
                log.warn("Пустой ответ от OpenAI. type={}, fileId={}, chatId={}", type, fileId, chatId);
                return BotErrors.ERROR_EMPTY_VOICE_MESSAGE;
            }
            return response.getText();
        } catch (Exception e) {
            log.error("Ошибка при переводе голосового сообщения. type={}, fileId={}, chatId={}", type, fileId, chatId, e);
            return BotErrors.ERROR_INTERNAL_TRANSLATION;
        } finally {
            try {
                if (fsr != null && fsr.getFile().exists()) {
                    Files.deleteIfExists(fsr.getFile().toPath());
                }
            } catch (IOException e) {
                log.warn("Не удалось удалить временный файл: {}", e.getMessage());
            }
        }
    }

    public String transcribeVideoNote(String fileId, Long chatId) {
        FileSystemResource fsr = null;
        try {
            var fileUrl = GetUrlVoice.getFileUrl(fileId);
            for (int attempt = 1; attempt <= 3; attempt++) {
                fsr = new FileSystemResource(new DownloadFileVideoNote().download(fileUrl, chatId));
                if (fsr.getFile().exists() && fsr.getFile().length() > 0) break;
                log.warn("Попытка {}: видеофайл ещё не готов, ожидаю...", attempt);
                Thread.sleep(1000L * attempt);
            }
            if (!fsr.getFile().exists()) {
                log.error("Файл видеокружка не найден или пуст. chatId={}, fileId={}", chatId, fileId);
                return BotErrors.ERROR_EMPTY_VIDEO_NOTE;
            }
            var entity = buildMultipartEntity(fsr);
            var response = sendRequestToWhisper(transcriptionUrl, entity, "video_note");
            if (response == null || response.getText().isEmpty()) {
                log.warn("Пустой ответ от OpenAI на видеокружок. fileId={}, chatId={}", fileId, chatId);
                return BotErrors.ERROR_EMPTY_VIDEO_NOTE;
            }
            return response.getText();
        } catch (Exception e) {
            log.error("Ошибка при расшифровке видеокружка. fileId={}, chatId={}", fileId, chatId, e);
            return BotErrors.ERROR_INTERNAL_VIDEO_NOTE;
        } finally {
            try {
                if (fsr != null && fsr.getFile().exists()) {
                    Files.deleteIfExists(fsr.getFile().toPath());
                }
            } catch (IOException e) {
                log.warn("Не удалось удалить временный файл видеокружка: {}", e.getMessage());
            }
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> buildMultipartEntity(FileSystemResource fsr) {
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("file", fsr);
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(map, headers);
    }

    private WhisperTranslatorResponse sendRequestToWhisper(String resourceUrl, HttpEntity<MultiValueMap<String, Object>> entity, String logContext) throws InterruptedException {
        WhisperTranslatorResponse response = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                response = new RestTemplate().postForObject(resourceUrl, entity, WhisperTranslatorResponse.class);
                break;
            } catch (HttpServerErrorException e) {
                log.warn("Попытка {}: ошибка 500 от OpenAI ({}). Повтор через {} мс", attempt, logContext, attempt * 1000);
                Thread.sleep(attempt * 1000);
            }
        }
        return response;
    }
}