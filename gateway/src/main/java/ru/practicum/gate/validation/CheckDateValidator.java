package ru.practicum.gate.validation;

import ru.practicum.gate.booking.dto.CreateBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<ru.practicum.gate.validation.StartBeforeEndDateValid, CreateBookingDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreateBookingDto createBookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = createBookingDto.getStart();
        LocalDateTime end = createBookingDto.getEnd();
        LocalDateTime now = LocalDateTime.now();
        if (start == null || end == null) {
            return false;
        }
        if (createBookingDto.getEnd().isBefore(createBookingDto.getStart()) ||
                createBookingDto.getEnd().isEqual(createBookingDto.getStart())) {
            return false;
        }
        if (createBookingDto.getStart().isBefore(LocalDateTime.now()) ||
                createBookingDto.getEnd().isBefore(LocalDateTime.now())) {
            return false;
        }
        if (start.isBefore(now) || end.isBefore(now)) {
            return false;
        }
        return start.isBefore(end);
    }
}

