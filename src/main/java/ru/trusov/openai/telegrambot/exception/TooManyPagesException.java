package ru.trusov.openai.telegrambot.exception;

public class TooManyPagesException extends RuntimeException {

    public TooManyPagesException(String message) {
        super(message);
    }
}