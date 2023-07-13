package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaRatingStorageImpl implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getAll() {
        return jdbcTemplate.query("SELECT * FROM mpa_rating", MpaRatingStorageImpl::makeRating);
    }

    @Override
    public MpaRating getOrThrow(long id) {
        final String sqlQuery = "SELECT * FROM mpa_rating WHERE id = ?";
        final List<MpaRating> ratings = jdbcTemplate.query(sqlQuery, MpaRatingStorageImpl::makeRating, id);

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

    private static MpaRating makeRating(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
}
