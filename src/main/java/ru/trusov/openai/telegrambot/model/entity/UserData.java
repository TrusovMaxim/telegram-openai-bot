package ru.trusov.openai.telegrambot.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "user_data")
@Data
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "data")
    private String data;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(name = "count_data")
    private Long countData;
    @Column(name = "dialog_date")
    private LocalDate dialogDate;
    @Column(name = "dialog_usage_today")
    private Integer dialogUsageToday;
}