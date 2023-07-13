package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;

import java.security.InvalidParameterException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorFilmStorageImpl implements DirectorFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getFilmDirectorsOrThrow(long filmId) {
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

    @Override
    public List<Film> getFilmOfDirectorSortedBy(long id, String sortKey) {
        switch (sortKey) {
            case "likes": {
                final String sqlQuery = "SELECT f.id,\n" +
                        "       f.name,\n" +
                        "       f.description,\n" +
                        "       f.release_date,\n" +
                        "       f.duration,\n" +
                        "       f.rating_id,\n" +
                        "       mr.name AS rating_name,\n" +
                        "       sq.likes_count\n" +
                        "FROM film AS f\n" +
                        "JOIN mpa_rating AS mr ON f.rating_id = mr.id\n" +
                        "LEFT JOIN (\n" +
                        "    SELECT film_id, COUNT(film_id) AS likes_count\n" +
                        "    FROM favorite_films\n" +
                        "    GROUP BY film_id\n" +
                        "    ) AS sq ON sq.film_id = f.id\n" +
                        "WHERE f.id IN (\n" +
                        "    SELECT film_id FROM director_film\n" +
                        "    WHERE director_id = ?\n" +
                        "    )\n" +
                        "ORDER BY sq.likes_count DESC;";
                return jdbcTemplate.query(sqlQuery, FilmStorageImpl::makeFilm, id);
            }
            case "year": {
                final String sqlQuery = "SELECT f.id,\n" +
                        "       f.name,\n" +
                        "       f.description,\n" +
                        "       f.release_date,\n" +
                        "       f.duration,\n" +
                        "       f.rating_id,\n" +
                        "       mr.name AS rating_name\n" +
                        "FROM film AS f\n" +
                        "JOIN mpa_rating AS mr ON f.rating_id = mr.id\n" +
                        "WHERE f.id IN (\n" +
                        "    SELECT film_id FROM director_film\n" +
                        "    WHERE director_id = ?\n" +
                        "    )\n" +
                        "ORDER BY YEAR(f.release_date);";
                return jdbcTemplate.query(sqlQuery, FilmStorageImpl::makeFilm, id);
            }
            default:
                throw new InvalidParameterException("Не правильно указан ключ сортировки SortBylike = " + sortKey);// поменять
        }
    }
}
