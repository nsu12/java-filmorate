package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
public class RatingDBStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<MPARating> getAll() {
        return jdbcTemplate.query("SELECT * FROM mpa_rating", RatingDBStorage::makeRating);
    }

    @Override
    public MPARating getOrThrow(long id) {
        final String sqlQuery = "SELECT * FROM mpa_rating WHERE id = ?";
        final List<MPARating> ratings = jdbcTemplate.query(sqlQuery, RatingDBStorage::makeRating, id);

        if (ratings.isEmpty()) {
            throw new EntryNotFoundException(
                    String.format("Рейтинг с id=%d не найден", id)
            );
        } else if (ratings.size() > 1) {
            throw new IllegalStateException();
        } else {
            return ratings.get(0);
        }
    }

    private static MPARating makeRating(ResultSet rs, int rowNum) throws SQLException {
        return new MPARating(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
}
