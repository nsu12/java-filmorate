package ru.yandex.practicum.filmorate.storage.likes;

public interface LikesStorage {

    boolean checkLikeIsExistsOrThrow(long userId, long filmId);

    void addLikeToFilmOrThrow(long userId, long filmId);

    void removeLikeFromFilmOrThrow(long userId, long filmId);
}
