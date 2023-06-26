package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.Collection;

public interface RatingStorage {
    Collection<MPARating> getAll();

    MPARating getOrThrow(long id);
}
