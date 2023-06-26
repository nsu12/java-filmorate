package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
public class GenreDBStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genre", GenreDBStorage::makeGenre);
    }

    @Override
    public Genre getOrThrow(long id) {
        final String sqlQuery = "SELECT * FROM genre WHERE id = ?";
        final List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDBStorage::makeGenre, id);

        if (genres.isEmpty()) {
            throw new EntryNotFoundException(
                    String.format("Жанр фильма с id=%d не найден", id)
            );
        } else if (genres.size() > 1) {
            throw new IllegalStateException();
        } else {
            return genres.get(0);
        }
    }

    public static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
            rs.getLong("id"),
            rs.getString("name")
        );
    }
}
