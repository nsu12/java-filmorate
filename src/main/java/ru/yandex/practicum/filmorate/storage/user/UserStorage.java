package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.EntryAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getAll();

    User get(long id) throws EntryNotFoundException;

    User add(User user) throws EntryAlreadyExistsException;

    void update(User user) throws EntryNotFoundException;

    void delete(long id) throws EntryNotFoundException;
}
