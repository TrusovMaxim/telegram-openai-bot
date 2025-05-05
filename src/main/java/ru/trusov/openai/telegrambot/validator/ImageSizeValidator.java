package ru.trusov.openai.telegrambot.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.trusov.openai.telegrambot.constant.BotOptions;

import java.util.Objects;

public class ImageSizeValidator implements ConstraintValidator<ValidImageSize, String> {
    @Override
    public void initialize(ValidImageSize constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String enteredText, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(enteredText, BotOptions.OPTION_IMAGE_SIZE_MINIMUM) | Objects.equals(enteredText, BotOptions.OPTION_IMAGE_SIZE_AVERAGE) | Objects.equals(enteredText, BotOptions.OPTION_IMAGE_SIZE_LARGE);
    }
}