package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService service;
    private final FilmService filmService;

    public UserController(UserService service, FilmService filmService) {
        this.service = service;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return service.create(user);
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return service.getUser(id);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return service.update(user);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        service.delete(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriendToUser(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        service.addFriendToUser(userId, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void removeFriendFromUser(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        service.removeFriendFromUser(userId, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable("id") Long userId) {
        return service.getUserFriends(userId);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public Collection<User> getUsersCommonFriends(
            @PathVariable("id") Long userId, @PathVariable("otherId") Long otherId
    ) {
        return service.getCommonFriendsForUsers(userId, otherId);
    }

    @GetMapping(value = "/users/{id}/recommendations")
    public Collection<Film> getFilmRecommendations(
            @PathVariable("id") Long userId
    ) {
        return filmService.getRecommendedFilms(userId);
    }
}
