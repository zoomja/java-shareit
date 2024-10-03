package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {

    private int id;
    private int itemId;
    private int bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
}
