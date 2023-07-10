package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorFilmStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikesStorage filmLikesStorage;
    private final EventService eventService;
    private final DirectorStorage directorStorage;
    private final DirectorFilmStorage directorFilmStorage;

    public Collection<Film> getAllFilms() {
        var films = filmStorage.getAll();
        films.forEach(film -> film.setGenres(filmGenreStorage.getFilmGenresOrThrow(film.getId())));
        films.forEach(film -> film.setDirectors(directorFilmStorage.getFilmDirectorsOrThrow(film.getId())));
        return films;
    }

    public Film create(@Valid Film film) {
        validateReleaseDate(film);
        Film filmCreated = filmStorage.addOrThrow(film);
        film.getGenres().forEach(genre -> filmGenreStorage.addGenreToFilmOrThrow(genre.getId(), filmCreated.getId()));
        film.getDirectors().forEach(director -> directorFilmStorage.addDirectorFromFilmOrThrow(director.getId(),filmCreated.getId()));
        filmCreated.setGenres(filmGenreStorage.getFilmGenresOrThrow(filmCreated.getId()));
        filmCreated.clearDirectors();
        filmCreated.setDirectors(directorFilmStorage.getFilmDirectorsOrThrow(filmCreated.getId()));
        return filmCreated;
    }

    public Film getById(long id) {
        Film film = filmStorage.getOrThrow(id);
        film.setGenres(filmGenreStorage.getFilmGenresOrThrow(id));
        film.setDirectors(directorFilmStorage.getFilmDirectorsOrThrow(id));
        return film;
    }

    public Film update(@Valid Film film) {
        validateReleaseDate(film);
        filmStorage.updateOrThrow(film);
        var oldGenres = filmGenreStorage.getFilmGenresOrThrow(film.getId());
        // remove all old genres which not present in incoming film
        oldGenres.stream()
                .filter(genre -> !film.getGenres().contains(genre))
                .collect(Collectors.toList())
                .forEach(genre -> filmGenreStorage.removeGenreFromFilmOrThrow(genre.getId(), film.getId()));
        // add genres which present in incoming film and not present in old list
        film.getGenres().stream()
                .filter(genre -> !oldGenres.contains(genre))
                .collect(Collectors.toList())
                .forEach(genre -> filmGenreStorage.addGenreToFilmOrThrow(genre.getId(), film.getId()));
        // update genres names in film
        film.setGenres(filmGenreStorage.getFilmGenresOrThrow(film.getId()));

        var oldDirectors = directorFilmStorage.getFilmDirectorsOrThrow(film.getId());
        oldDirectors.stream()
                .filter(director -> !film.getDirectors().contains(director))
                .collect(Collectors.toList())
                .forEach(director -> directorFilmStorage.removeDirectorFromFilmOrThrow(director.getId(),film.getId()));
        film.getDirectors().stream()
                .filter(director -> !oldDirectors.contains(director))
                .collect(Collectors.toList())
                .forEach(director -> directorFilmStorage.addDirectorFromFilmOrThrow(director.getId(),film.getId()));
        film.clearDirectors();
        film.setDirectors(directorFilmStorage.getFilmDirectorsOrThrow(film.getId()));

        return film;
    }

    public void delete(Long filmId) {
        filmStorage.deleteOrThrow(filmId);
    }

    private void validateReleaseDate(Film film) throws ValidationException {
        final var firstFilmDate = LocalDate.of(1895, 12, 25);
        if (film.getReleaseDate().isBefore(firstFilmDate)) {
            log.error("Film '{}' is released before 1985-12-25", film.getName());
            throw new ValidationException("Invalid film release date");
        }
    }

    public void addLikeFromUser(long filmId, long userId) {
        final Film film = filmStorage.getOrThrow(filmId);
        final User user = userStorage.getOrThrow(userId);
        filmLikesStorage.addLikeToFilmOrThrow(userId, filmId);
        log.debug("Film '{}' liked by user '{}'", film.getName(), user.getLogin());
        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
    }

    public void removeLikeFromUser(long filmId, long userId) {
        final Film film = filmStorage.getOrThrow(filmId);
        final User user = userStorage.getOrThrow(userId);
        filmLikesStorage.removeLikeFromFilmOrThrow(userId, filmId);
        log.debug("User '{}' remove like from film '{}'", user.getName(), film.getName());
        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
    }

    public Collection<Film> getListOfPopular(Integer count, Long genreId, Long year) {
        Set<Film> films = new HashSet<>();
        if (genreId != 0 && year != 0) {
            Collection<Film> film = filmStorage.getPopularFilmsByYearAndGenre(count, year, genreId);
            films.addAll(film);
        } else if (genreId != 0 && year == 0) {
            Collection<Film> film = filmStorage.getPopularFilmsByGenre(count, genreId);
            films.addAll(film);
        } else if (genreId == 0 && year != 0) {
            Collection<Film> film = filmStorage.getPopularFilmsByYear(count, year);
            films.addAll(film);
        } else {
            films.addAll(filmStorage.getPopularFilms(count));
        }

        films.forEach(film -> film.setGenres(filmGenreStorage.getFilmGenresOrThrow(film.getId())));
        films.forEach(film -> film.setDirectors(directorFilmStorage.getFilmDirectorsOrThrow(film.getId())));

        return films;
    }

    public Collection<Film> getListOfCommon(long userId, long friendId) {
        final var films = filmStorage.getCommonFilmsSortedByPopularity(userId, friendId);
        films.forEach(film -> film.setGenres(filmGenreStorage.getFilmGenresOrThrow(film.getId())));
        return films;
    }

    public Collection<Film> getRecommendedFilms(long id) {
        var films = filmStorage.getRecommendedFilms(id);
        films.forEach(film -> film.setGenres(filmGenreStorage.getFilmGenresOrThrow(film.getId())));
        return films;
    }

    public Collection<Film> getListFilmOfDirectorSortedBy(Long id, String value) {
        directorStorage.getById(id);
        List<Film> films = directorFilmStorage.getFilmOfDirectorSortedBy(id, value);
        films.forEach(film -> film.setGenres(filmGenreStorage.getFilmGenresOrThrow(film.getId())));
        films.forEach(film -> film.setDirectors(directorFilmStorage.getFilmDirectorsOrThrow(film.getId())));
        return films;
    }

    private List<Film> findFilmByYear(Long year) {
        List<Film> films = new ArrayList<>();
        for (Film film : filmStorage.getAll()) {
            if (film.getReleaseDate().getYear() == year) {
                films.add(film);
            }
        }
        return films;
    }
}
