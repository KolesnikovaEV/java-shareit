package ru.practicum.gate.exception;

public class NotValidDateException extends RuntimeException {
    public NotValidDateException(String message) {
        super(message);
    }
}
