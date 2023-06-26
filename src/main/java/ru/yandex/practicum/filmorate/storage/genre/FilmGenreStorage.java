package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {
    List<Genre> getFilmGenresOrThrow(long filmId);

    void addGenreToFilmOrThrow(long genreId, long filmId);

    void removeGenreFromFilmOrThrow(long genreId, long filmId);
}
