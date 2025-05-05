package ru.trusov.openai.telegrambot.util.file;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DownloadFileVoice {

    public String downloadFile(String urlVoice, Long chatId) throws IOException {
        var sourceFileName = "src/main/resources/audio/" + chatId + "audio.oga";
        var targetFileName = "src/main/resources/audio/" + chatId + "audio.mp3";
        FileUtils.copyURLToFile(new URL(urlVoice), new File(sourceFileName));
        var converterFile = new ConverterFile("src/main/resources/ffmpeg");
        converterFile.convertOgaToMp3(sourceFileName, targetFileName);
        Files.delete(Path.of(sourceFileName));
        return targetFileName;
    }

    public FileSystemResource downloadAsResource(String urlVoice, Long chatId) throws IOException {
        var path = downloadFile(urlVoice, chatId);
        return new FileSystemResource(new File(path));
    }
}