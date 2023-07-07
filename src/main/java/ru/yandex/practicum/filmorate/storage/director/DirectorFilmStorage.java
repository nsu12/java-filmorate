package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorFilmStorage {
    List<Director> getDirectorFilmOrThrow(long id);

    void removeDirectorFromFilmOrThrow(long id, long id1);

    void addDirectorFromFilmOrThrow(long directorId, long filmId);

}
