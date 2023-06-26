package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class UserDBStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM user_account", UserDBStorage::makeUser);
    }

    @Override
    public User getOrThrow(long id) {
        final String sqlQuery = "SELECT * FROM user_account WHERE id = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, UserDBStorage::makeUser, id);

        if (users.isEmpty()) {
            throw new EntryNotFoundException(
                    String.format("Пользователь с id %d не найден", id)
            );
        } else if (users.size() > 1) {
            throw new IllegalStateException();
        } else {
            return users.get(0);
        }
    }

    @Override
    public User addOrThrow(User user) {
        String sqlQuery = "INSERT INTO user_account (login, name, email, birthday) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                    stmt.setString(1, user.getLogin());
                    stmt.setString(2, user.getName());
                    stmt.setString(3, user.getEmail());
                    stmt.setDate(4, Date.valueOf(user.getBirthday()));
                    return stmt;
                },
                keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return user;
    }

    @Override
    public void updateOrThrow(User user) {
        String sqlQuery = "UPDATE user_account SET login = ?, email = ?, name = ?, birthday = ?  WHERE id = ?";
        int numUpdated = jdbcTemplate.update(
                sqlQuery,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        if (numUpdated == 0) {
            throw new EntryNotFoundException(
                    String.format(
                            "Не удалось обновить информацию о пользователе с id %d - пользователь не найден",
                            user.getId()
                    )
            );
        }
    }

    @Override
    public void deleteOrThrow(long id) {
        if (jdbcTemplate.update("DELETE FROM user_account WHERE id = ?", id) == 0) {
            throw new EntryNotFoundException(
                    String.format("Не удалось удалить пользователя с id %d - пользователь не найден", id)
            );
        }
    }

    public static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
