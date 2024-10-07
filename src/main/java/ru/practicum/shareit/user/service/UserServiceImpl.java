package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new IllegalArgumentException("Email не может быть null");
        }
        if (isEmailAlreadyUsed(userDto.getEmail(), -1)) {
            throw new IllegalArgumentException("Email уже используется: " + userDto.getEmail());
        }
        User user = userMapper.toUser(userDto);

        userRepository.save(user);
        log.info("Пользователь создан: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {

        User user = userMapper.toUser(getUser(id));

        if (userDto.getEmail() != null && isEmailAlreadyUsed(userDto.getEmail(), user.getId())) {
            throw new IllegalArgumentException("Email уже используется: " + userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
        log.info("Пользователь обновлен: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(int id) {
        User user = userRepository.findById(id).orElseThrow(()-> new NotFoundException("Пользователь не найден"));
        log.info("Пользователь получен по ID: {}", id);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toUserDto(userRepository.findAll());
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
        log.info("Пользователь удален с ID: {}", id);
    }

    private boolean isEmailAlreadyUsed(String email, int currentUserId) {
        if (email == null) {
            return false;
        }
        return userRepository.findAll().stream()
                .anyMatch(user -> email.equals(user.getEmail()) && user.getId() != currentUserId);
    }
}
