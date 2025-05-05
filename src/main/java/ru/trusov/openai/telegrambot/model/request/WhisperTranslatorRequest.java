package ru.trusov.openai.telegrambot.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class WhisperTranslatorRequest implements Serializable {
    private String model;
    private MultipartFile file;
}