package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Director add(Director director);

    Director update(Director director);

    Director getById(Long id);

    Collection<Director> getAll();

    void delete(Long id);
}
