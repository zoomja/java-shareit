package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(int id,UserDto userDto);

    UserDto getUser(int userId);

    List<UserDto> getAllUsers();

    void deleteUser(int id);

}
