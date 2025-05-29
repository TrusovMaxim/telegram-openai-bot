package ru.trusov.openai.telegrambot.model.dto.record;

import ru.trusov.openai.telegrambot.model.entity.User;

import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public record UsageLimitCheckParams(
        User user,
        LocalDate usageDate,
        int limit,
        Consumer<LocalDate> dateSetter,
        IntConsumer counterSetter,
        Supplier<Integer> counterGetter,
        String warningMessage,
        Long chatId
) {
}