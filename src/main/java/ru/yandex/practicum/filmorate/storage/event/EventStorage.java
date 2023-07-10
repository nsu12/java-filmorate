package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventStorage {
    /**
     * Возвращает все событий для пользователя с указанным id
     * @param userId userId идентификатор пользователя
     * @return список событий
     */
    Collection<Event> getAllForUserOrThrow(long userId);

    /**
     * Добавляет событие в хранилище
     * @param event экземпляр объекта события
     */
    void addOrThrow(Event event);
}
