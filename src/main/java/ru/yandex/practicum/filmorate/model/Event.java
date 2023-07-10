package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;

@Data
@Builder
public class Event {
    private long eventId;
    private long timestamp;
    @Positive
    private long userId;
    private EventType eventType;
    private EventOperation operation;
    private long entityId;
}
