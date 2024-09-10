package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NoArgsConstructor
public class Booking {
    private int id;
    private int itemId;
    private int bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}