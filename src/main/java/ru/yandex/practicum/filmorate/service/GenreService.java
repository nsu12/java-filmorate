package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;

import java.util.Collection;

@Service
public class GenreService {

    private final GenreDBStorage genreStorage;

    @Autowired
    public GenreService(GenreDBStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getById(long id) {
        return genreStorage.getOrThrow(id);
    }

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAll();
    }
}
