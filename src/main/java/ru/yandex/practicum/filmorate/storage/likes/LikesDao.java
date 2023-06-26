package ru.yandex.practicum.filmorate.storage.likes;

public interface LikesDao {
    void addLikeToFilmOrThrow(long userId, long filmId);

    void removeLikeFromFilmOrThrow(long userId, long filmId);
}
