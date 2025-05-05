package ru.trusov.openai.telegrambot.service.user;

import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.enums.TranslatorTypeEnum;
import ru.trusov.openai.telegrambot.model.entity.User;

public interface UserService {
    User getUser(Long chatId);

    void updateBotStateEnum(User user, BotStateEnum botStateEnum);

    void updateSettingTranslatorEnum(User user, TranslatorTypeEnum translatorTypeEnum);

    void updateSettingImageEnum(User user, ImageSizeEnum imageSizeEnum);

    void registerUser(String userName, String firstName, String lastName, Long chatId);
}