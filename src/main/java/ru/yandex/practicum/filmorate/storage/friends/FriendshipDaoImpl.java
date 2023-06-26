package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.util.Collection;

@Component
public class FriendshipDaoImpl implements FriendshipDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getUserFriendsOrThrow(long userId) {
        final String sqlQuery = "SELECT * FROM user_account " +
                "WHERE id IN (SELECT friend_id FROM user_friends WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, UserDBStorage::makeUser, userId);
    }

    @Override
    public void addFriendOrThrow(long userId, long friendId) {
        jdbcTemplate.update("INSERT INTO user_friends (user_id, friend_id, confirmed) " +
                "VALUES (?, ?, ?)", userId, friendId, false);
    }

    @Override
    public void removeFriendOrThrow(long userId, long friendId) {
        jdbcTemplate.update("DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?",
                userId, friendId);
    }
}
