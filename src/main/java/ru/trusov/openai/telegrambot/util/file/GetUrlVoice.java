package ru.trusov.openai.telegrambot.util.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.trusov.openai.telegrambot.config.telegram.ApplicationContextProvider;
import ru.trusov.openai.telegrambot.util.json.JsonTool;

import java.io.IOException;

@Getter
@Component
@AllArgsConstructor
public class GetUrlVoice {
    private final String appTelegramBotToken;

    private static JSONObject getFileRequest(String fileIdVoice) throws IOException {
        var client = ApplicationContextProvider.getContext().getBean(GetUrlVoice.class);
        var fileUrl = String.format("https://api.telegram.org/bot%s/getFile?file_id=%s",
                client.getAppTelegramBotToken(),
                fileIdVoice);
        return JsonTool.readJsonFromUrl(fileUrl);
    }

    public static String getFileUrl(String fileIdVoice) throws IOException {
        var client = ApplicationContextProvider.getContext().getBean(GetUrlVoice.class);
        var jsonObject = getFileRequest(fileIdVoice);
        return String.format("https://api.telegram.org/file/bot%s/%s",
                client.getAppTelegramBotToken(),
                jsonObject.get("file_path"));
    }
}