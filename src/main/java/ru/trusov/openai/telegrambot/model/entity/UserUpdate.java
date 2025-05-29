package ru.trusov.openai.telegrambot.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_update")
@Data
@NoArgsConstructor
public class UserUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "update_code")
    private String updateCode;

    public UserUpdate(Long userId, String updateCode) {
        this.userId = userId;
        this.updateCode = updateCode;
    }
}