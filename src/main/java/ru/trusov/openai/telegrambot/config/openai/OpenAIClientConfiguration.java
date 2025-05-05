package ru.trusov.openai.telegrambot.config.openai;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Indexed;

@Configuration
@Indexed
@Data
@Slf4j
public class OpenAIClientConfiguration {
    @Value("${APP_OPEN_AI_READ_TIMEOUT}")
    private int readTimeout;
    @Value("${APP_OPEN_AI_CONNECT_TIMEOUT}")
    private int connectTimeout;
    @Value("${APP_OPEN_AI_TOKEN}")
    private String token;
    @Value("${APP_OPEN_AI_MODEL}")
    private String model;
    @Value("${APP_OPEN_AI_AUDIO_MODEL}")
    private String audioModel;

    @Bean
    public String appOpenAiToken(@Value("${APP_OPEN_AI_TOKEN}") String token) {
        return token;
    }

    @Bean
    public String appOpenAiModel(@Value("${APP_OPEN_AI_MODEL}") String model) {
        return model;
    }

    @Bean
    public String resourceOpenAiUrlTranscription(@Value("${RESOURCE_OPEN_AI_URL_TRANSCRIPTION}") String resourceOpenAiUrlTranscription) {
        return resourceOpenAiUrlTranscription;
    }

    @Bean
    public String resourceOpenAiUrlTranslation(@Value("${RESOURCE_OPEN_AI_URL_TRANSLATION}") String resourceOpenAiUrlTranslation) {
        return resourceOpenAiUrlTranslation;
    }

    @Bean
    public String resourceOpenAiUrlGenerateImage(@Value("${RESOURCE_OPEN_AI_URL_GENERATE_IMAGE}") String resourceOpenAiUrlGenerateImage) {
        return resourceOpenAiUrlGenerateImage;
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(getConnectTimeout(), getReadTimeout());
    }

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }

    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return request -> request.header("Authorization", "Bearer " + token);
    }
}