package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FilmService filmService;
    private final EventService eventService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.create(user);
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriendToUser(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        userService.addFriendToUser(userId, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void removeFriendFromUser(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        userService.removeFriendFromUser(userId, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Long userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getUsersCommonFriends(
            @PathVariable("id") Long userId, @PathVariable("otherId") Long otherId
    ) {
        return userService.getCommonFriendsForUsers(userId, otherId);
    }

    @GetMapping(value = "/{id}/recommendations")
    public List<Film> getFilmRecommendations(
            @PathVariable("id") Long userId
    ) {
        return filmService.getRecommendedFilms(userId);
    }

    @GetMapping(value = "/{id}/feed")
    public List<Event> getFeed(
            @PathVariable("id") Long userId
    ) {
        return eventService.getAllByUserId(userId);
    }
}
