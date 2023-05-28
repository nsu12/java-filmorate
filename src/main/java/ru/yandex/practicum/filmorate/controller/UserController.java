package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping(value = "/users")
    public Collection<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        return service.create(user);
    }

    @GetMapping(value = "/users/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return service.getUser(id);
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
        return service.update(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriendToUser(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        service.addFriendToUser(userId, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void removeFriendFromUser(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        service.removeFriendFromUser(userId, friendId);
    }

    @GetMapping(value = "/users/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable("id") Long userId) {
        return service.getUserFriends(userId);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public Collection<User> getUsersCommonFriends(
            @PathVariable("id") Long userId, @PathVariable("otherId") Long otherId
    ) {
        return service.getCommonFriendsForUsers(userId, otherId);
    }
}
