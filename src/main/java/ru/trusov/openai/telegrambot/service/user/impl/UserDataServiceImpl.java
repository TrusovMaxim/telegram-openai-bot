package ru.trusov.openai.telegrambot.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.entity.UserData;
import ru.trusov.openai.telegrambot.repository.UserDataRepository;
import ru.trusov.openai.telegrambot.service.user.UserDataService;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserDataServiceImpl implements UserDataService {
    private final UserDataRepository userDataRepository;

    @Override
    @Transactional
    public void createData(User user, String data) {
        var userData = new UserData();
        userData.setUser(user);
        userData.setData(data);
        userData.setCountData(0L);
        userDataRepository.save(userData);
    }

    @Override
    public UserData findByUser(User user) {
        return userDataRepository.findByUser(user).orElse(null);
    }

    @Override
    @Transactional
    public void save(UserData userData) {
        userDataRepository.save(userData);
    }

    @Override
    @Transactional
    public void resetUserDialog(User user) {
        var data = findByUser(user);
        if (data != null) {
            data.setData(null);
            data.setCountData(0L);
            save(data);
        }
    }
}