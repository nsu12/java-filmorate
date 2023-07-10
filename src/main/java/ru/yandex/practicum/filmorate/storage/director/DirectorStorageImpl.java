package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorStorageImpl implements DirectorStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director add(Director director) {
        String sqlQuery = "INSERT INTO DIRECTOR (name) " +
                "VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                    stmt.setString(1, director.getName());
                    return stmt;
                },
                keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery =
                "UPDATE director " +
                        "SET name = ? " +
                        "WHERE id = ?";

        int numUpdated = jdbcTemplate.update(
                sqlQuery,
                director.getName(),
                director.getId()
        );
        if (numUpdated == 0) {
            throw new EntryNotFoundException(
                    String.format(
                            "Не удалось обновить информацию о директоре с id %d - Директор с таким id не найден",
                            director.getId()
                    )
            );
        }
        return getById(director.getId());
    }

    @Override
    public Director getById(Long id) {
        final String sqlQuery =
                "SELECT " +
                        "d.id, d.name " +
                        "FROM director AS d " +
                        "WHERE d.id = ?";
        final List<Director> directors = jdbcTemplate.query(sqlQuery, DirectorStorageImpl::makeDirector, id);

        if (directors.isEmpty()) {
            throw new EntryNotFoundException(
                    String.format("Директор с id %d не найден", id)
            );
        } else if (directors.size() > 1) {
            throw new IllegalStateException();
        } else {
            return directors.get(0);
        }
    }

    @Override
    public Collection<Director> getAll() {
        return jdbcTemplate.query("SELECT * FROM director", DirectorStorageImpl::makeDirector);
    }

    @Override
    public void delete(Long id) {
        if (jdbcTemplate.update("DELETE FROM director WHERE id = ?", id) == 0) {
            throw new EntryNotFoundException(
                    String.format("Не удалось удалить директора с id %d - директор с таким id не найден", id)
            );
        }
    }

    public static Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("id"));
        director.setName(rs.getString("name"));
        return director;
    }
}
