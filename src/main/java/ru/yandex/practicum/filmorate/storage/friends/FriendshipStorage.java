package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendshipStorage {
    Collection<User> getUserFriendsOrThrow(long userId);

    void addFriendOrThrow(long userId, long friendId);

    void removeFriendOrThrow(long userId, long friendId);
}
