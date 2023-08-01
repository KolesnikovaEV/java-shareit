package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import java.time.LocalDateTime;

import static ru.practicum.shareit.constant.Constants.DATE_PATTERN;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEndDateValid
public class CreateBookingDto {
    private Long itemId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDateTime end;
}
