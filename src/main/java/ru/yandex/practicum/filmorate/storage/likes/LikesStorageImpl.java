package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikesStorageImpl implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean checkLikeIsExistsOrThrow(long userId, long filmId) {
        final Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM favorite_films WHERE user_id = ? AND film_id = ?",
                Integer.class, userId, filmId);
        if (count == null) {
            throw new IllegalStateException();
        }
        return count == 1;
    }

    @Override
    public void addLikeToFilmOrThrow(long userId, long filmId) {
        jdbcTemplate.update("INSERT INTO favorite_films (user_id, film_id) " +
                "VALUES (?, ?)", userId, filmId);
    }

    @Override
    public void removeLikeFromFilmOrThrow(long userId, long filmId) {
        jdbcTemplate.update("DELETE FROM favorite_films WHERE user_id = ? AND film_id = ?",
                userId, filmId);
    }
}
