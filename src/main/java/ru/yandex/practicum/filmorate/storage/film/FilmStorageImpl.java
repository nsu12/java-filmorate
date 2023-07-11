package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmStorageImpl implements FilmStorage {
    private static final String GET_SEARCH_FILMS_BY_NAME = "SELECT f.*, rm.name AS rating_name, " +
            "GROUP_CONCAT(DISTINCT Concat(g.id, '-', g.name) ORDER BY Concat(g.id,'-',g.name)) AS genre_id_name, " +
            "GROUP_CONCAT(DISTINCT Concat(d.id, '-', d.name) ORDER BY Concat(d.id, '-', d.name)) AS director_id_name " +
            "FROM (" +
            "  SELECT fi.* " +
            "        FROM film fi " +
            "        LEFT JOIN " +
            "        (SELECT film_id, COUNT(*) ff " +
            "            FROM favorite_films " +
            "            GROUP BY film_id" +
            "        ) fil " +
            "        ON fil.film_id = fi.id " +
            "        ORDER BY ff DESC" +
            ") f " +
            "LEFT JOIN mpa_rating rm ON f.rating_id = rm.id " +
            "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.id " +
            "LEFT JOIN director_film fd ON f.id = fd.film_id " +
            "LEFT JOIN director d ON fd.director_id = d.id " +
            "WHERE LOWER(f.name) LIKE ? " +
            "GROUP BY f.id " +
            "ORDER BY f.id DESC";

    private static final String GET_SEARCH_FILMS_BY_DIRECTOR = "SELECT f.*, rm.name AS rating_name, " +
            "GROUP_CONCAT(DISTINCT Concat(g.id, '-', g.name) ORDER BY Concat(g.id,'-',g.name)) AS genre_id_name, " +
            "GROUP_CONCAT(DISTINCT Concat(d.id, '-', d.name) ORDER BY Concat(d.id, '-', d.name)) AS director_id_name " +
            "FROM (" +
            "  SELECT fi.* " +
            "        FROM film fi " +
            "        LEFT JOIN " +
            "        (SELECT film_id, COUNT(*) ff " +
            "            FROM favorite_films " +
            "            GROUP BY film_id" +
            "        ) fil " +
            "        ON fil.film_id = fi.id " +
            "        ORDER BY ff DESC" +
            ") f " +
            "LEFT JOIN mpa_rating rm ON f.rating_id = rm.id " +
            "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.id " +
            "LEFT JOIN director_film fd ON f.id = fd.film_id " +
            "LEFT JOIN director d ON fd.director_id = d.id " +
            "WHERE LOWER(d.name) LIKE ? " +
            "GROUP BY f.id " +
            "ORDER BY f.id DESC";

    private static final String GET_SEARCH_FILMS_BY_ALL = "SELECT f.*, rm.name AS rating_name, " +
            "GROUP_CONCAT(DISTINCT Concat(g.id, '-', g.name) ORDER BY Concat(g.id,'-',g.name)) AS genre_id_name, " +
            "GROUP_CONCAT(DISTINCT Concat(d.id, '-', d.name) ORDER BY Concat(d.id, '-', d.name)) AS director_id_name " +
            "FROM (" +
            "  SELECT fi.* " +
            "        FROM film fi " +
            "        LEFT JOIN " +
            "        (SELECT film_id, COUNT(*) ff " +
            "            FROM favorite_films " +
            "            GROUP BY film_id" +
            "        ) fil " +
            "        ON fil.film_id = fi.id " +
            "        ORDER BY ff DESC" +
            ") f " +
            "LEFT JOIN mpa_rating rm ON f.rating_id = rm.id " +
            "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
            "LEFT JOIN genre g ON fg.genre_id = g.id " +
            "LEFT JOIN director_film fd ON f.id = fd.film_id " +
            "LEFT JOIN director d ON fd.director_id = d.id " +
            "WHERE LOWER(f.name) LIKE ? " +
            "OR LOWER(d.name) LIKE ? " +
            "GROUP BY f.id " +
            "ORDER BY f.id DESC";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addOrThrow(Film film) {
        String sqlQuery = "INSERT INTO FILM (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                    stmt.setString(1, film.getName());
                    stmt.setString(2, film.getDescription());
                    stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                    stmt.setLong(4, film.getDuration());
                    stmt.setLong(5, film.getMpa().getId());
                    return stmt;
                },
                keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return film;
    }

    @Override
    public Film getOrThrow(long id) {
        final String sqlQuery =
                "SELECT " +
                 "   f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, mr.name AS rating_name " +
                 "FROM film AS f " +
                 "JOIN mpa_rating AS mr ON f.rating_id = mr.id " +
                 "WHERE f.id = ?";
        final List<Film> films = jdbcTemplate.query(sqlQuery, FilmStorageImpl::makeFilm, id);

        if (films.isEmpty()) {
            throw new EntryNotFoundException(
                    String.format("Фильм с id %d не найден", id)
            );
        } else if (films.size() > 1) {
            throw new IllegalStateException();
        } else {
            return films.get(0);
        }
    }

    @Override
    public Collection<Film> getAll() {
        return jdbcTemplate.query(
                "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, mr.name AS rating_name " +
                 "FROM film AS f " +
                 "JOIN mpa_rating AS mr ON f.rating_id = mr.id",
                FilmStorageImpl::makeFilm);
    }

    @Override
    public void updateOrThrow(Film film) {
        String sqlQuery =
                "UPDATE film " +
                        "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                        "WHERE id = ?";
        int numUpdated = jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (numUpdated == 0) {
            throw new EntryNotFoundException(
                    String.format(
                            "Не удалось обновить информацию о фильме с id %d - фильм с таким id не найден",
                            film.getId()
                    )
            );
        }
    }

    @Override
    public void deleteOrThrow(long id) {
        if (jdbcTemplate.update("DELETE FROM film WHERE id = ?", id) == 0) {
            throw new EntryNotFoundException(
                    String.format("Не удалось удалить фильм с id %d - фильм с таким id не найден", id)
            );
        }
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        final String sqlQuery =
                "SELECT f.id, " +
                        "   f.name," +
                        "   f.description," +
                        "   f.release_date, " +
                        "   f.duration, " +
                        "   f.rating_id, " +
                        "   mr.name AS rating_name, " +
                        "   sq.likes_count " +
                        "FROM film AS f " +
                        "JOIN mpa_rating AS mr ON f.rating_id = mr.id " +
                        "LEFT JOIN (" +
                        "       SELECT film_id, COUNT(film_id) AS likes_count" +
                        "       FROM favorite_films" +
                        "       GROUP BY film_id) AS sq ON sq.film_id = f.id " +
                        "ORDER BY sq.likes_count DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, FilmStorageImpl::makeFilm, count);
    }

    @Override
    public Collection<Film> getPopularFilmsByGenre(int count, long genreId) {
        final String sqlQuery =
                "SELECT f.id, " +
                        "   f.name," +
                        "   f.description," +
                        "   f.release_date, " +
                        "   f.duration, " +
                        "   f.rating_id, " +
                        "   mr.name AS rating_name, " +
                        "   sq.likes_count " +
                        "FROM film AS f " +
                        "JOIN mpa_rating AS mr ON f.rating_id = mr.id " +
                        "LEFT JOIN (" +
                        "       SELECT film_id, COUNT(film_id) AS likes_count" +
                        "       FROM favorite_films" +
                        "       GROUP BY film_id) AS sq ON sq.film_id = f.id " +
                        "WHERE  f.id IN (" +
                        "        select film_id " +
                        "        from film_genre " +
                        "WHERE genre_id = ?) " +
                        "ORDER BY sq.likes_count DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, FilmStorageImpl::makeFilm, genreId, count);
    }

    @Override
    public Collection<Film> getPopularFilmsByYear(int count, int year) {
        final String sqlQuery =
                "SELECT f.id, " +
                        "   f.name," +
                        "   f.description," +
                        "   f.release_date, " +
                        "   f.duration, " +
                        "   f.rating_id, " +
                        "   mr.name AS rating_name, " +
                        "   sq.likes_count " +
                        "FROM film AS f " +
                        "JOIN mpa_rating AS mr ON f.rating_id = mr.id " +
                        "LEFT JOIN (" +
                        "       SELECT film_id, COUNT(film_id) AS likes_count" +
                        "       FROM favorite_films" +
                        "       GROUP BY film_id) AS sq ON sq.film_id = f.id " +
                        "WHERE YEAR(f.release_date) = ? " +
                        "ORDER BY sq.likes_count DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, FilmStorageImpl::makeFilm, year, count);
    }

    @Override
    public Collection<Film> getPopularFilmsByYearAndGenre(int count, int year, long genreId) {
        final String sqlQuery =
                "SELECT f.id, " +
                        "   f.name," +
                        "   f.description," +
                        "   f.release_date, " +
                        "   f.duration, " +
                        "   f.rating_id, " +
                        "   mr.name AS rating_name, " +
                        "   sq.likes_count " +
                        "FROM film AS f " +
                        "JOIN mpa_rating AS mr ON f.rating_id = mr.id " +
                        "JOIN film_genre AS fg ON fg.film_id = f.id " +
                        "LEFT JOIN (" +
                        "       SELECT film_id, COUNT(film_id) AS likes_count" +
                        "       FROM favorite_films" +
                        "       GROUP BY film_id) AS sq ON sq.film_id = f.id " +
                        "WHERE YEAR(f.release_date) = ? " +
                        "AND fg.genre_id = ? " +
                        "ORDER BY sq.likes_count DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, FilmStorageImpl::makeFilm, year, genreId, count);
    }

    @Override
    public Collection<Film> getCommonFilmsSortedByPopularity(long user1Id, long user2Id) {
        final String sqlQuery =
                "SELECT f.id, " +
                        "   f.name," +
                        "   f.description," +
                        "   f.release_date, " +
                        "   f.duration, " +
                        "   f.rating_id, " +
                        "   mr.name AS rating_name, " +
                        "   sq.likes_count " +
                        "FROM film AS f " +
                        "JOIN mpa_rating AS mr ON f.rating_id = mr.id " +
                        "LEFT JOIN (" +
                        "       SELECT film_id, COUNT(film_id) AS likes_count" +
                        "       FROM favorite_films" +
                        "       GROUP BY film_id) AS sq ON sq.film_id = f.id " +
                        "WHERE film_id IN (" +
                        "       SELECT film_id FROM favorite_films" +
                        "       WHERE user_id = ? OR user_id = ?" +
                        "       GROUP BY film_id" +
                        "       HAVING COUNT(film_id) = 2) " +
                        "ORDER BY sq.likes_count DESC ";
        return jdbcTemplate.query(sqlQuery, FilmStorageImpl::makeFilm, user1Id, user2Id);
    }

    @Override
    public Collection<Film> getRecommendedFilms(long id) {
        long maxCount = 0L;
        long theMostSimilarUserId = 0L;
        List<Film> recommendations = new ArrayList<>();

        List<Long> filmIdUser = getIdFilmsListFromUserFilm(id);

        String user = "select user_id from favorite_films ";
        List<Long> userIdWhoStayLike = jdbcTemplate.query(user, this::mapRowToUserIdWhoStayLike);

        for (Long userId : userIdWhoStayLike) {
            long count = 0L;
            if (Objects.equals(userId, id)) {
                continue;
            }
            List<Long> filmListIdUserWhoStayLike = getIdFilmsListFromUserFilm(userId);

            for (Long integer : filmListIdUserWhoStayLike) {
                if (filmIdUser.contains(integer)) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                theMostSimilarUserId = userId;
            }
        }

        List<Long> filmIdUserWithMaxCross = new ArrayList<>();
        if (theMostSimilarUserId != 0) {
            filmIdUserWithMaxCross = getIdFilmsListFromUserFilm(theMostSimilarUserId);
        }

        for (Long idUserWithMaxCross : filmIdUserWithMaxCross) {
            if (!filmIdUser.contains(idUserWithMaxCross)) {
                recommendations.add(getOrThrow(idUserWithMaxCross));
            }
        }
        return recommendations;
    }

    public static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(new MpaRating(rs.getLong("rating_id"), rs.getString("rating_name")));
        return film;
    }

    @Override
    public List<Film> searchFilms(Optional<String> query, List<String> by) {
        List<Film> searchedFilms = new ArrayList<>();
        if (query.get().isEmpty() || query.get().equals(" ")) {
            return searchedFilms;
        }
        String stringInSql = "%" + query.get().toLowerCase() + "%";
        if (by != null) {
            log.debug("Получен запрос с параметром by");
            if (by.size() == 1 & by.contains("title")) {
                log.debug("Получен запрос на поиск фильма по названию");
                return getSearchedFilms(GET_SEARCH_FILMS_BY_NAME, stringInSql);
            }
            if (by.size() == 1 & by.contains("director")) {
                log.debug("Получен запрос на поиск фильма по имени режиссера");
                return getSearchedFilms(GET_SEARCH_FILMS_BY_DIRECTOR, stringInSql);
            }
            if (by.size() == 2 & by.contains("title") & by.contains("director")) {
                log.debug("Получен запрос на поиск фильма по имени режиссера и по названию фильма");
                searchedFilms = jdbcTemplate.query(GET_SEARCH_FILMS_BY_ALL, (rs, rowNum) -> makeFilm(rs,rowNum), stringInSql, stringInSql);
                log.debug("Результаты поиска:");
                for (Film film : searchedFilms) {
                    log.debug("Фильм с film_id={}: {}", film.getId(), film);
                }
                return searchedFilms;
            } else {
                throw new IllegalArgumentException("Передан некорректный параметр by!");
            }
        }
        log.debug("Получен запрос без параметра by, выполнен поиск по умолчанию");
        return getSearchedFilms(GET_SEARCH_FILMS_BY_NAME, stringInSql);
    }

    private List<Film> getSearchedFilms(String sql, String stringInSql) {
        List<Film> searchedFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs,rowNum), stringInSql);
        log.debug("Результаты поиска:");
        for (Film film : searchedFilms) {
            log.debug("Фильм с film_id={}: {}", film.getId(), film);
        }
        return searchedFilms;
    }


    private Long mapRowToUserIdWhoStayLike(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("user_id");
    }

    private List<Long> getIdFilmsListFromUserFilm(Long id) {
        String sql = "select film_id from favorite_films where USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToFilmId, id);
    }

    private Long mapRowToFilmId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("film_id");
    }
}
