package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorageImpl;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorageImpl genreStorage;

    public Genre getById(long id) {
        return genreStorage.getOrThrow(id);
    }

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAll();
    }
}
