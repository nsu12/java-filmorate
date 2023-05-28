package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User create(@Valid User user) {
        validateUserName(user);
        final User createdUser = storage.add(user);
        log.info("User '{}' created", createdUser.getLogin());
        return createdUser;
    }

    public User update(@Valid User user) {
        validateUserName(user);
        storage.update(user);
        log.info("User '{}' successfully updated", user.getLogin());
        return user;
    }

    public Collection<User> getAllUsers() {
        return storage.getAll();
    }

    public User getUser(long id) {
        return storage.get(id);
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("User '{}': name field is empty - use login instead", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    public void addFriendToUser(long userId, long friendId) {
         final User user = storage.get(userId);
         final User friend = storage.get(friendId);
         user.getFriends().add(friend.getId());
         storage.update(user);
         friend.getFriends().add(user.getId());
         storage.update(friend);
         log.info("User '{}' added to friends to '{}'", friend.getLogin(), user.getLogin());
    }

    public void removeFriendFromUser(long userId, long friendId) {
        final User user = storage.get(userId);
        if (user.getFriends().remove(friendId)) {
            storage.update(user);
            log.info("User '{}' removed from friends of '{}'", storage.get(friendId).getLogin(), user.getLogin());
        }
    }

    public Collection<User> getUserFriends(long userId) {
        final var userFriends = storage.get(userId).getFriends();
        return userFriends.stream().map(storage::get).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriendsForUsers(long userId, long otherId) {
        final var userFriends = storage.get(userId).getFriends();
        final var otherUserFriends = storage.get(otherId).getFriends();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(storage::get)
                .collect(Collectors.toList());
    }
}
