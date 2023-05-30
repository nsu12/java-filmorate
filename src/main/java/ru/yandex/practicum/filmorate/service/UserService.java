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
        final User createdUser = storage.addOrThrow(user);
        log.info("User '{}' created", createdUser.getLogin());
        return createdUser;
    }

    public User update(@Valid User user) {
        validateUserName(user);
        storage.updateOrThrow(user);
        log.info("User '{}' successfully updated", user.getLogin());
        return user;
    }

    public Collection<User> getAllUsers() {
        return storage.getAll();
    }

    public User getUser(long id) {
        return storage.getOrThrow(id);
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("User '{}': name field is empty - use login instead", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    public void addFriendToUser(long userId, long friendId) {
         final User user = storage.getOrThrow(userId);
         final User friend = storage.getOrThrow(friendId);
         user.getFriends().add(friend.getId());
         storage.updateOrThrow(user);
         friend.getFriends().add(user.getId());
         storage.updateOrThrow(friend);
         log.info("User '{}' added to friends to '{}'", friend.getLogin(), user.getLogin());
    }

    public void removeFriendFromUser(long userId, long friendId) {
        final User user = storage.getOrThrow(userId);
        if (user.getFriends().remove(friendId)) {
            storage.updateOrThrow(user);
            log.info("User '{}' removed from friends of '{}'", storage.getOrThrow(friendId).getLogin(), user.getLogin());
        }
    }

    public Collection<User> getUserFriends(long userId) {
        final var userFriends = storage.getOrThrow(userId).getFriends();
        return userFriends.stream().map(storage::getOrThrow).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriendsForUsers(long userId, long otherId) {
        final var userFriends = storage.getOrThrow(userId).getFriends();
        final var otherUserFriends = storage.getOrThrow(otherId).getFriends();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(storage::getOrThrow)
                .collect(Collectors.toList());
    }
}
