package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaRatingStorage {
    Collection<MpaRating> getAll();

    MpaRating getOrThrow(long id);
}
