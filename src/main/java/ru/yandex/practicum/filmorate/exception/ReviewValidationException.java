package ru.yandex.practicum.filmorate.exception;

import javax.validation.ValidationException;

public class ReviewValidationException extends ValidationException {
    public ReviewValidationException(String message) {
        super(message);
    }
}
