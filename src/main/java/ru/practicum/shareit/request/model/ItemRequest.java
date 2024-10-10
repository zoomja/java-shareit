package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
public class ItemRequest {
    private int id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
