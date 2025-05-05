package ru.trusov.openai.telegrambot.service.user;

import ru.trusov.openai.telegrambot.model.entity.User;
import ru.trusov.openai.telegrambot.model.entity.UserData;

public interface UserDataService {
    void createData(User user, String data);

    UserData findByUser(User user);

    void save(UserData userData);

    void resetUserDialog(User user);
}