package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class FilmGenreDaoImpl implements FilmGenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getFilmGenresOrThrow(long filmId) {
        final String sqlQuery = "SELECT * FROM genre " +
                "WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?) ORDER BY id";
        return jdbcTemplate.query(sqlQuery, GenreDBStorage::makeGenre, filmId);
    }

    @Override
    public void addGenreToFilmOrThrow(long genreId, long filmId) {
        jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES ( ?, ? )", filmId, genreId);
    }

    @Override
    public void removeGenreFromFilmOrThrow(long genreId, long filmId) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?", filmId, genreId);
    }
}
