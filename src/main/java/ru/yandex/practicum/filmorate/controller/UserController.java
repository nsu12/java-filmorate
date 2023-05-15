package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final List<User> users = new ArrayList<>();

    @GetMapping
    public List<User> getAll() {
        return users;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        users.add(user);
        user.setId(users.size());
        log.info("User {} created", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (user.getId() > users.size()) {
            throw new DataNotFoundException("the user is not found");
        }

        validate(user);
        users.set(user.getId() - 1, user);
        log.info("User {} successfully updated", user.getLogin());
        return user;
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("User {}: name field is empty - use login instead", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
