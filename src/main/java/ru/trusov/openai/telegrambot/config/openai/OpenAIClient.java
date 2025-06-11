package ru.trusov.openai.telegrambot.config.openai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.config.telegram.ApplicationContextProvider;
import ru.trusov.openai.telegrambot.constant.BotErrors;
import ru.trusov.openai.telegrambot.constant.BotPrompts;
import ru.trusov.openai.telegrambot.model.common.Message;
import ru.trusov.openai.telegrambot.model.request.ChatGPTRequest;
import ru.trusov.openai.telegrambot.service.openai.OpenAIClientApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Getter
@Component
@AllArgsConstructor
public class OpenAIClient {
    private final String appOpenAiToken;
    private final String appOpenAiModel;

    public static String runOpenAI(String conversation) {
        var client = ApplicationContextProvider.getContext().getBean(OpenAIClient.class);
        var service = new OpenAiService(client.getAppOpenAiToken());
        var messages = new ArrayList<ChatMessage>();
        var lines = conversation.split("\n");
        for (String line : lines) {
            if (line.startsWith("User: ")) {
                messages.add(new ChatMessage(ChatMessageRole.USER.value(), line.substring(6)));
            } else if (line.startsWith("Bot: ")) {
                messages.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(), line.substring(5)));
            }
        }
        var chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model(client.getAppOpenAiModel())
                .messages(messages)
                .n(1)
                .logitBias(new HashMap<>())
                .build();
        var response = new StringBuilder();
        service.streamChatCompletion(chatCompletionRequest)
                .blockingSubscribe(
                        data -> {
                            var choices = data.getChoices();
                            if (!choices.isEmpty()) {
                                var content = choices.getFirst().getMessage().getContent();
                                if (content != null) {
                                    response.append(content);
                                }
                            }
                        },
                        error -> {
                            log.error("Ошибка при обращении к OpenAI: {}", error.getMessage(), error);
                            response.append("Ошибка, попробуйте позже!");
                        }
                );
        return response.toString();
    }

    public static String summarize(String text) {
        try {
            var prompt = BotPrompts.PROMPT_FILE_SUMMARIZE.formatted(text);
            var message = Message.builder()
                    .role("user")
                    .content(prompt)
                    .build();
            var chatRequest = ChatGPTRequest.builder()
                    .model("gpt-4o-mini")
                    .messages(List.of(message))
                    .build();
            var response = ApplicationContextProvider.getContext()
                    .getBean(OpenAIClientApiService.class)
                    .chat(chatRequest);
            return response.getChoiceResponses().getFirst().getMessage().getContent();
        } catch (Exception e) {
            log.error("Ошибка при получении резюме из OpenAI: {}", e.getMessage(), e);
            return BotErrors.ERROR_FILE_PROCESSING_FAILED;
        }
    }
}