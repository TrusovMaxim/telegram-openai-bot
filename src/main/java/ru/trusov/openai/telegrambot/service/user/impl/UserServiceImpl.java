package ru.trusov.openai.telegrambot.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trusov.openai.telegrambot.constant.BotUpdates;
import ru.trusov.openai.telegrambot.model.entity.UserUpdate;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.enums.TranslatorTypeEnum;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.repository.UserRepository;
import ru.trusov.openai.telegrambot.repository.UserUpdateRepository;
import ru.trusov.openai.telegrambot.service.bot.MessageSenderService;
import ru.trusov.openai.telegrambot.service.user.UserService;
import ru.trusov.openai.telegrambot.util.time.TimeUtil;

import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MessageSenderService messageSenderService;
    private final UserUpdateRepository userUpdateRepository;

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
        user.setVoiceUsageToday(0);
        user.setVideoNoteUsageToday(0);
        user.setYoutubeUsageToday(0);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void notifyAllUsersAboutNewFeatures() {
        var users = userRepository.findAll();
        var updates = userUpdateRepository.findAll();
        var updatesByUserId = updates.stream().collect(Collectors.toMap(UserUpdate::getUserId, Function.identity()));
        var sentCount = 0;
        for (User user : users) {
            var existingUpdate = updatesByUserId.get(user.getId());
            if (existingUpdate != null && BotUpdates.CODE_UPDATE.equals(existingUpdate.getUpdateCode())) continue;
            try {
                messageSenderService.send(BotUpdates.TEXT_UPDATE, user.getChatId());
                if (existingUpdate != null) {
                    existingUpdate.setUpdateCode(BotUpdates.CODE_UPDATE);
                    userUpdateRepository.save(existingUpdate);
                } else {
                    var newUpdate = new UserUpdate(user.getId(), BotUpdates.CODE_UPDATE);
                    userUpdateRepository.save(newUpdate);
                }
                sentCount++;
            } catch (Exception e) {
                log.warn("Не удалось отправить уведомление пользователю {}: {}", user.getChatId(), e.getMessage());
            }
        }
        log.info("Рассылка об обновлении '{}' завершена. Уведомлений отправлено: {}", BotUpdates.CODE_UPDATE, sentCount);
    }
}