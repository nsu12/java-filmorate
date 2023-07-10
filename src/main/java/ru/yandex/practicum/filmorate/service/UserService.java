package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage storage;
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final EventService eventService;

    public User create(@Valid User user) {
        validateUserName(user);
        final User createdUser = storage.addOrThrow(user);
        log.debug("User '{}' created", createdUser.getLogin());
        return createdUser;
    }

    public User update(@Valid User user) {
        validateUserName(user);
        storage.updateOrThrow(user);
        log.debug("User '{}' successfully updated", user.getLogin());
        return user;
    }

    public Collection<User> getAllUsers() {
        return storage.getAll();
    }

    public User getUserById(long id) {
        return userStorage.getOrThrow(id);
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("User '{}': name field is empty - use login instead", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    public void addFriendToUser(long userId, long friendId) {
         final User user = storage.getOrThrow(userId);
         final User friend = storage.getOrThrow(friendId);
         friendshipStorage.addFriendOrThrow(userId, friendId);
         log.debug("User '{}' added to friends to '{}'", friend.getLogin(), user.getLogin());
         eventService.createEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
    }

    public void removeFriendFromUser(long userId, long friendId) {
        final User user = storage.getOrThrow(userId);
        friendshipStorage.removeFriendOrThrow(userId, friendId);
        log.debug("User '{}' removed from friends of '{}'", storage.getOrThrow(friendId).getLogin(), user.getLogin());
        eventService.createEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    public Collection<User> getUserFriends(long userId) {
        storage.getOrThrow(userId); // check user presence
        return friendshipStorage.getUserFriendsOrThrow(userId);
    }

    public Collection<User> getCommonFriendsForUsers(long userId, long otherId) {
        final var userFriends = friendshipStorage.getUserFriendsOrThrow(userId);
        final var otherUserFriends = friendshipStorage.getUserFriendsOrThrow(otherId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        storage.deleteOrThrow(id);
    }
}
