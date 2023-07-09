package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.hql.internal.ast.InvalidPathException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;

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
    public List<Film> getFilmOfDirectorSortedBy(Long id, String value) {
        switch (value){
        case "likes": {
            final String sqlQuery = "SELECT * FROM FILM as f\n" +
                    "\tLEFT JOIN (SELECT film_id, COUNT(film_id) AS likes_count \n" +
                    "\tFROM favorite_films\n" +
                    "\tGROUP BY film_id) AS sq ON sq.film_id = f.id\n" +
                    "\tJOIN mpa_rating AS mr ON f.rating_id = mr.id\n" +
                    "WHERE f.ID IN\n" +
                    "\t(SELECT FILM_ID FROM director_film WHERE director_id = ?)\n" +
                    "\tORDER BY sq.likes_count DESC\n";
           var x= jdbcTemplate.query(sqlQuery, FilmStorageImpl::makeFilm,id);
           return x;
        }
        case "year": break;
            default: throw new RuntimeException();// поменять
        }
        return null;
    }
}
