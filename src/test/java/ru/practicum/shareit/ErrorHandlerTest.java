package ru.practicum.shareit;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTest {
    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void testHandleNotFound() {
        NotFoundException notFoundException = new NotFoundException("Resource not found");
        ErrorResponse result = errorHandler.handlePostNotFoundException(notFoundException);
        assertEquals("Resource not found", result.getError());
    }

    @Test
    void testHandleConflict() {
        ConflictException alreadyExistsException = new ConflictException("Resource already exists");
        ConflictException result = errorHandler.handleForConflict(alreadyExistsException);
        assertEquals("Произошел конфликт данных: Resource already exists", result.getMessage());
    }

    @Test
    void testHandleThrowable() {
        Throwable throwable = new Throwable("Что-то пошло не так");
        ErrorResponse result = errorHandler.handleThrowable(throwable);
        assertEquals("Произошла непредвиденная ошибка: Что-то пошло не так", result.getError());
    }

    @Test
    void testHandleConstraintViolationException() {
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        ConstraintViolation<String> violation = ConstraintViolationImpl.forBeanValidation(
                "400 BAD_REQUEST",
                Collections.emptyMap(),
                Collections.emptyMap(),
                "constraintMessage",
                String.class,
                "rootBean",
                null,
                "value",
                null,
                null,
                null
        );
        constraintViolations.add(violation);

        ConstraintViolationException exception = new ConstraintViolationException(constraintViolations);

        Map<String, String> result = errorHandler.handleThrowable1(exception);

        assertEquals(HttpStatus.BAD_REQUEST.toString(), result.get("error"));
    }
}
