package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public void createEvent(long userId, EventType type, EventOperation operation, long entityId) {
        eventStorage.addOrThrow(Event.builder()
                        .userId(userId)
                        .eventType(type)
                        .operation(operation)
                        .entityId(entityId)
                        .timestamp(Instant.now().toEpochMilli())
                        .build()
        );
        log.info("Добавлено событие {} для пользователя {}: {} {}",
                type.toString(), userId, operation.toString(), entityId);
    }

    public Collection<Event> getAllByUserId(long userId) {
        userStorage.getOrThrow(userId);
        var events = eventStorage.getAllForUserOrThrow(userId);
        log.info("Получено {} событий для пользователя {}", events.size(), userId);
        return events;
    }
}
