package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    /**
     * Добавление фильма в хранилище
     * @param film объект \ref Film который должен быть добавлен
     * @return объект Film у которого заполнено поле id
     */
    Film add(Film film);

    Film get(long id);

    Collection<Film> getAll();

    void update(Film film);

    void remove(long id);
}
