package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorFilmStorage {
    List<Director> getFilmDirectorsOrThrow(long id);

    void removeDirectorFromFilmOrThrow(long id, long id1);

    void addDirectorFromFilmOrThrow(long directorId, long filmId);

    List<Film> getFilmOfDirectorSortedBy(long id, String sortKey);
}
