package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreStorageImpl implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genre", GenreStorageImpl::makeGenre);
    }

    @Override
    public Genre getOrThrow(long id) {
        final String sqlQuery = "SELECT * FROM genre WHERE id = ?";
        final List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreStorageImpl::makeGenre, id);

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
