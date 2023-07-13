package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventStorageImpl implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getAllForUserOrThrow(long userId) {
        String sqlQuery = "SELECT * FROM event WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, EventStorageImpl::makeEvent, userId);
    }

    @Override
    public void addOrThrow(Event event) {
        String sqlQuery = "INSERT INTO event (user_id, event_type, operation, ts, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sqlQuery,
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getTimestamp(),
                event.getEntityId()
                );
    }

    private static Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("operation")))
                .timestamp(rs.getLong("ts"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
