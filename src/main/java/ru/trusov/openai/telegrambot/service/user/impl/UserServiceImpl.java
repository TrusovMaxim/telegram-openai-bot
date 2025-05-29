package ru.trusov.openai.telegrambot.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.enums.TranslatorTypeEnum;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.repository.UserRepository;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.util.time.TimeUtil;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUser(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Override
    @Transactional
    public void updateBotStateEnum(User user, BotStateEnum botStateEnum) {
        user.setBotStateEnum(botStateEnum);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateSettingTranslatorEnum(User user, TranslatorTypeEnum translatorTypeEnum) {
        user.setSettingTranslator(translatorTypeEnum);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateSettingImageEnum(User user, ImageSizeEnum imageSizeEnum) {
        user.setSettingImage(imageSizeEnum);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void registerUser(String userName, String firstName, String lastName, Long chatId) {
        var user = new User(chatId);
        user.setUserName(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBotStateEnum(BotStateEnum.START);
        user.setCurrentTime(TimeUtil.nowInMoscow());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }
}