package ru.trusov.openai.telegrambot.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.trusov.openai.telegrambot.model.enums.BotStateEnum;
import ru.trusov.openai.telegrambot.model.enums.ImageSizeEnum;
import ru.trusov.openai.telegrambot.model.enums.TranslatorTypeEnum;

import java.util.Date;

@Entity
@Table(name = "user_bot")
@Data
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "last_bot_state")
    @Enumerated(EnumType.STRING)
    private BotStateEnum botStateEnum;
    @Column(name = "setting_translator")
    @Enumerated(EnumType.STRING)
    private TranslatorTypeEnum settingTranslator;
    @Column(name = "setting_image")
    @Enumerated(EnumType.STRING)
    private ImageSizeEnum settingImage;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date currentTime;

    public User(Long chatId) {
        this.chatId = chatId;
    }
}