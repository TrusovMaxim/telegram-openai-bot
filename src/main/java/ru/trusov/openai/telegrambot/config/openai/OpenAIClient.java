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

import java.util.ArrayList;
import java.util.HashMap;

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
}