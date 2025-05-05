package ru.trusov.openai.telegrambot.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.trusov.openai.telegrambot.constant.BotErrors;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageSizeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ValidImageSize {
    String message() default BotErrors.ERROR_IMAGE_FORMAT_UNSUPPORTED;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}