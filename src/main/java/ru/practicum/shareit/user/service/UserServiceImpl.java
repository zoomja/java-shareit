package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Map<Integer, User> users = new HashMap<>();
    private final UserMapper userMapper;

    private int userIdCounter = 1;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new IllegalArgumentException("Email не может быть null");
        }
        if (isEmailAlreadyUsed(userDto.getEmail(), -1)) {
            throw new IllegalArgumentException("Email уже используется: " + userDto.getEmail());
        }
        User user = userMapper.toUser(userDto);
        user.setId(userIdCounter++);

        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        int userId = Math.toIntExact(id);

        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        User user = users.get(userId);
        user.setName(userDto.getName());

        if (userDto.getEmail() != null && isEmailAlreadyUsed(userDto.getEmail(), userId)) {
            throw new IllegalArgumentException("Email уже используется: " + userDto.getEmail());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        users.put(userId, user);
        log.info("Пользователь обновлен: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        log.info("Пользователь получен по ID: {}", id);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> userList = new ArrayList<>(users.values());
        log.info("Получение всех пользователей, количество: {}", userList.size());
        return userMapper.toUserDto(userList);
    }

    @Override
    public void deleteUser(int id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        users.remove(id);
        log.info("Пользователь удален с ID: {}", id);
    }

    private boolean isEmailAlreadyUsed(String email, int currentUserId) {
        if (email == null) {
            return false;
        }
        return users.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()) && user.getId() != currentUserId);
    }

    public boolean existsById(int userId) {
        return users.containsKey(userId);
    }
}
