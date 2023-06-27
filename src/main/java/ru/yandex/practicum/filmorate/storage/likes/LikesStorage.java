package ru.yandex.practicum.filmorate.storage.likes;

public interface LikesStorage {
    void addLikeToFilmOrThrow(long userId, long filmId);

    void removeLikeFromFilmOrThrow(long userId, long filmId);
}
