package ru.trusov.openai.telegrambot.telegram.processor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import ru.trusov.openai.telegrambot.config.openai.OpenAIClient;
import ru.trusov.openai.telegrambot.constant.*;
import ru.trusov.openai.telegrambot.exception.TooManyPagesException;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.UserActionPathEnum;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserDataService;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.util.file.ConcurrencyLimiter;
import ru.trusov.openai.telegrambot.util.file.DownloadFileUtil;
import ru.trusov.openai.telegrambot.util.file.GetUrlVoice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class PdfProcessor {
    private final UserService userService;
    private final UserDataService userDataService;
    private final MessageSenderService messageSenderService;
    private final ConcurrencyLimiter concurrencyLimiter;

    public void process(User user, Long chatId, UserActionPathEnum action) {
        if (action != null) {
            switchSection(user, chatId, action);
            return;
        }
        messageSenderService.send(BotErrors.ERROR_FILE_EXPECTED, chatId);
    }

    private void switchSection(User user, Long chatId, UserActionPathEnum action) {
        switch (action) {
            case CHAT_GPT -> {
                userService.updateBotStateEnum(user, BotStateEnum.CHAT_GPT);
                messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_GPT, chatId);
            }
            case RESET_GPT_DIALOG -> {
                userService.updateBotStateEnum(user, BotStateEnum.CHAT_GPT);
                userDataService.resetUserDialog(user);
                messageSenderService.send(BotSectionState.STATE_CHAT_GPT_DIALOG_RESET, chatId);
            }
            case TRANSLATOR -> {
                userService.updateBotStateEnum(user, BotStateEnum.TRANSLATOR);
                if (user.getSettingTranslator() == null) {
                    messageSenderService.sendTranslatorPrompt(chatId);
                } else {
                    messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_TRANSLATOR, chatId);
                }
            }
            case YOUTUBE -> {
                userService.updateBotStateEnum(user, BotStateEnum.YOUTUBE);
                messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_YOUTUBE, chatId);
            }
            case IMAGE -> {
                userService.updateBotStateEnum(user, BotStateEnum.IMAGE);
                if (user.getSettingImage() == null) {
                    messageSenderService.sendImagePrompt(chatId);
                } else {
                    messageSenderService.send(BotSectionState.STATE_CHAT_SWITCHED_TO_IMAGE, chatId);
                }
            }
            case FILE_SUMMARIZE -> messageSenderService.send(BotSectionState.STATE_CHAT_ALREADY_IN_SECTION, chatId);
            case SETTINGS -> messageSenderService.sendSettingsMenu(chatId);
            case BUY_IMAGES -> messageSenderService.sendImageInvoice(chatId);
            case BALANCE -> messageSenderService.send(
                    MessageFormat.format(BotMessages.MESSAGE_IMAGE_BALANCE_CURRENT, user.getImageBalance()), chatId);
            case BUY_PREMIUM -> messageSenderService.sendPremiumInvoice(chatId);
            case INFO -> messageSenderService.sendInfoWithButtons(chatId);
            case FEEDBACK -> {
                userService.updateBotStateEnum(user, BotStateEnum.FEEDBACK);
                messageSenderService.send(BotPrompts.PROMPT_FEEDBACK_WRITE, chatId);
            }
            case COMMANDS -> messageSenderService.sendCommandMenu(chatId);
            case SETTING_VOICE -> messageSenderService.sendTranslatorPrompt(chatId);
            case SETTING_IMAGE -> messageSenderService.sendImagePrompt(chatId);
            case DONATE -> messageSenderService.send(BotMessages.MESSAGE_DONATE_INFO, chatId);
            case ABOUT_AUTHOR -> messageSenderService.send(BotMessages.MESSAGE_ABOUT_AUTHOR, chatId);
        }
    }

    public void handleFile(User user, Long chatId, Document doc) {
        if (!"application/pdf".equals(doc.getMimeType())) {
            messageSenderService.send(BotErrors.ERROR_FILE_EXPECTED, chatId);
            return;
        }
        var isPremium = Boolean.TRUE.equals(user.getIsPremium()) &&
                user.getPremiumEnd() != null &&
                LocalDateTime.now().isBefore(user.getPremiumEnd());
        if (!isPremium) {
            messageSenderService.send(BotWarnings.WARNING_FILE_ONLY_FOR_PREMIUM, chatId);
            return;
        }
        var taskType = "file_summarize";
        var userId = user.getId();
        concurrencyLimiter.executeLimited(() -> {
            Path path = null;
            try {
                messageSenderService.send(BotSectionState.STATE_FILE_PROCESSING, chatId);
                var fileUrl = GetUrlVoice.getFileUrl(doc.getFileId());
                var downloaded = DownloadFileUtil.download(fileUrl, chatId);
                path = downloaded.toPath();
                var rawText = extractText(path);
                var summary = OpenAIClient.summarize(rawText);
                messageSenderService.send(BotSectionState.STATE_FILE_SUMMARY_PREFIX + summary, chatId);
            } catch (TooManyPagesException e) {
                messageSenderService.send(BotErrors.ERROR_FILE_TOO_LARGE, chatId);
            } catch (Exception e) {
                log.error("Ошибка при обработке PDF: {}", e.getMessage(), e);
                messageSenderService.send(BotErrors.ERROR_FILE_PROCESSING_FAILED, chatId);
            } finally {
                if (path != null) {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        log.warn("Не удалось удалить временный PDF-файл: {}", e.getMessage());
                    }
                }
            }
            return null;
        }, taskType, userId, chatId, msg -> messageSenderService.send(msg, chatId));
    }

    public static String extractText(Path path) {
        try (var doc = PDDocument.load(path.toFile())) {
            if (doc.getNumberOfPages() > 10) {
                throw new TooManyPagesException("PDF слишком длинный");
            }
            var stripper = new PDFTextStripper();
            return stripper.getText(doc);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось прочитать PDF", e);
        }
    }
}