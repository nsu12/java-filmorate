package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getAll();

    User getOrThrow(long id);

    User addOrThrow(User user);

    void updateOrThrow(User user);

    void deleteOrThrow(long id);
}
