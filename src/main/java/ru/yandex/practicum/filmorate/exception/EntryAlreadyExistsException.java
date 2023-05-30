package ru.yandex.practicum.filmorate.exception;

public class EntryAlreadyExistsException extends RuntimeException {
    public EntryAlreadyExistsException(String message) {
        super(message);
    }
}
