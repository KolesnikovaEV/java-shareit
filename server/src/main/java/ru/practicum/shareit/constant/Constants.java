package ru.practicum.shareit.constant;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Comparator;

@UtilityClass
public class Constants {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public static final Comparator<Booking> orderByStartDateDesc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return -1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return 1;
        } else {
            return 0;
        }
    };

    public static final Comparator<Booking> orderByStartDateAsc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return 1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return -1;
        } else {
            return 0;
        }
    };
}
