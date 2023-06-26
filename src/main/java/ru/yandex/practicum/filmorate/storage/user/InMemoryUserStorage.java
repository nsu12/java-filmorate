package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntryAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private long nextId = 1;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getOrThrow(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new EntryNotFoundException(
                    String.format("Пользователь с id %d не найден", id)
            );
        }
        return user;
    }

    @Override
    public User addOrThrow(User user) {
        if (user.getId() != 0 && users.containsKey(user.getId())) {
            throw new EntryAlreadyExistsException(
                    String.format("Пользователь с id %d уже существует", user.getId())
            );
        }
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void updateOrThrow(User user) {
        if (users.remove(user.getId()) == null) {
            throw new EntryNotFoundException(
                    String.format(
                            "Не удалось обновить информацию о пользователе с id %d - пользователь не найден",
                            user.getId()
                    )
            );
        }
        users.put(user.getId(), user);
    }

    @Override
    public void deleteOrThrow(long id) {
        if (users.remove(id) == null) {
            throw new EntryNotFoundException(
                    String.format("Не удалось удалить пользователя с id %d - пользователь не найден", id)
            );
        }
    }
}
