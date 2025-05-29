package ru.trusov.openai.telegrambot.util.time;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeUtil {

    public static Date nowInMoscow() {
        return Date.from(ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toInstant());
    }
}