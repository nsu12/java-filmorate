package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
@Component
@Slf4j
@RequiredArgsConstructor

public class DirectorFilmStorageImpl implements DirectorFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getDirectorFilmOrThrow(long filmId) {
        final String sqlQuery = "SELECT * FROM director " +
                "WHERE id IN (SELECT director_id FROM director_film WHERE film_id = ?) ORDER BY id";
        return jdbcTemplate.query(sqlQuery, DirectorStorageImpl::makeDirector, filmId);
    }

    @Override
    public void removeDirectorFromFilmOrThrow(long directorId, long filmId) {
        jdbcTemplate.update("DELETE FROM director_film WHERE film_id = ? AND director_id = ?", filmId, directorId);
    }

    @Override
    public void addDirectorFromFilmOrThrow(long directorId, long filmId) {
        jdbcTemplate.update("INSERT INTO director_film (film_id, director_id) VALUES ( ?, ? )", filmId, directorId);
    }
}
