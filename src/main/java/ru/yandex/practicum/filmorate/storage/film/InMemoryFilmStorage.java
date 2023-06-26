package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.EntryAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private long nextId = 1;

    @Autowired
    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    @Override
    public Film addOrThrow(Film film) {
        if (film.getId() != 0 && films.containsKey(film.getId())) {
            throw new EntryAlreadyExistsException(
                    String.format("Фильм %s с id - %d уже существует", film.getName(), film.getId())
            );
        }

        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getOrThrow(long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new EntryNotFoundException(
                    String.format("Фильм с id %d не найден", id)
            );
        }

        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public void updateOrThrow(Film film) {
        if (films.remove(film.getId()) == null) {
            throw new EntryNotFoundException(
                    String.format(
                            "Не удалось обновить информацию о фильме с id %d - фильм с таким id не найден",
                            film.getId()
                    )
            );
        }
        films.put(film.getId(), film);
    }

    @Override
    public void removeOrThrow(long id) {
        if (films.remove(id) == null) {
            throw new EntryNotFoundException(
                    String.format("Не удалось удалить фильм с id %d - фильм с таким id не найден", id)
            );
        }
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return null;
    }
}
