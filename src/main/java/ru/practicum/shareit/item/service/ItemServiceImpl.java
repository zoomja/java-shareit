package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final Map<Integer, Item> items = new HashMap<>();
    private final ItemMapper itemMapper;
    private final UserServiceImpl userServiceImpl;
    private int itemIdCounter = 1;

    public ItemDto createItem(int userId, ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new IllegalArgumentException("Название предмета не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание предмета не может быть пустым");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("Некорректный ID владельца: " + userId);
        }
        if (!userServiceImpl.existsById(userId)) {
            throw new NotFoundException("Владелец с ID " + userId + " не найден");
        }

        Item item = itemMapper.toItem(itemDto);
        item.setId(itemIdCounter++);
        item.setOwnerId(userId); // Устанавливаем владельца предмета

        items.put(item.getId(), item);
        log.info("Предмет создан: {}", item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }
        if (item.getOwnerId() != userId) {
            throw new NotFoundException("Пользователь с ID " + userId + " не имеет прав на обновление этого предмета");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        items.put(itemId, item);
        log.info("Предмет обновлен: {}", item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(int itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return items.values().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(int userId) {
        if (!userServiceImpl.existsById(userId)) {
            throw new NotFoundException("Владелец с ID " + userId + " не найден");
        }
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
