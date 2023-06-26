package ru.yandex.practicum.filmorate.storage.likes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LikesDaoImpl implements LikesDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
