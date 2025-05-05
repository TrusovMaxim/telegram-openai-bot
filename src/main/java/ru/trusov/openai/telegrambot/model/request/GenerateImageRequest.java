package ru.trusov.openai.telegrambot.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.trusov.openai.telegrambot.validator.ValidImageSize;

@Data
@Builder
public class GenerateImageRequest {
    @NotBlank
    private String prompt;
    @ValidImageSize
    private String size;
    @Min(1)
    @Max(10)
    private int n;
}