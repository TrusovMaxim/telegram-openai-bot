package ru.trusov.openai.telegrambot.util.file;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class ConverterFile {
    private final FFmpeg ffmpeg;

    public ConverterFile(@Value("${ffmpeg.path}") String ffmpegPath) throws IOException {
        this.ffmpeg = new FFmpeg(new File(ffmpegPath).getPath());
    }

    public void convertOgaToMp3(String inputPath, String targetPath) throws IOException {
        var builder = new FFmpegBuilder()
                .setInput(inputPath)
                .overrideOutputFiles(true)
                .addOutput(targetPath)
                .setAudioCodec("libmp3lame")
                .setAudioBitRate(32768)
                .done();
        var executor = new FFmpegExecutor(ffmpeg);
        try {
            executor.createJob(builder).run();
        } catch (IllegalArgumentException e) {
            log.warn("FFmpeg завершился с ошибкой: {}", e.getMessage());
        }
    }
}