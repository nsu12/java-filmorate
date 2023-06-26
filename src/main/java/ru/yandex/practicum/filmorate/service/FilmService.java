package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.likes.LikesDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmGenreDao filmGenres;
    private final LikesDao filmLikes;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       FilmGenreDao filmGenres,
                       LikesDao filmLikes) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmGenres = filmGenres;
        this.filmLikes = filmLikes;
    }

    public Collection<Film> getAllFilms() {
        var films = filmStorage.getAll();
        films.forEach(film -> film.setGenres(filmGenres.getFilmGenresOrThrow(film.getId())));
        return films;
    }

    public Film create(@Valid Film film) {
        validateReleaseDate(film);
        Film filmCreated = filmStorage.addOrThrow(film);
        film.getGenres().forEach(genre -> filmGenres.addGenreToFilmOrThrow(genre.getId(), filmCreated.getId()));
        filmCreated.setGenres(filmGenres.getFilmGenresOrThrow(filmCreated.getId()));
        return filmCreated;
    }

    public Film getById(long id) {
        Film film = filmStorage.getOrThrow(id);
        film.setGenres(filmGenres.getFilmGenresOrThrow(id));
        return film;
    }

    public Film update(@Valid Film film) {
        validateReleaseDate(film);
        filmStorage.updateOrThrow(film);
        var oldGenres = filmGenres.getFilmGenresOrThrow(film.getId());
        // remove all old genres which not present in incoming film
        oldGenres.stream()
                .filter(genre -> !film.getGenres().contains(genre))
                .collect(Collectors.toList())
                .forEach(genre -> filmGenres.removeGenreFromFilmOrThrow(genre.getId(), film.getId()));
        // add genres which present in incoming film and not present in old list
        film.getGenres().stream()
                .filter(genre -> !oldGenres.contains(genre))
                .collect(Collectors.toList())
                .forEach(genre -> filmGenres.addGenreToFilmOrThrow(genre.getId(), film.getId()));
        // update genres names in film
        film.setGenres(filmGenres.getFilmGenresOrThrow(film.getId()));
        return film;
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
        filmLikes.addLikeToFilmOrThrow(userId, filmId);
        log.info("Film '{}' liked by user '{}'", film.getName(), user.getLogin());
    }

    public void removeLikeFromUser(long filmId, long userId) {
        final Film film = filmStorage.getOrThrow(filmId);
        final User user = userStorage.getOrThrow(userId);
        filmLikes.removeLikeFromFilmOrThrow(userId, filmId);
        log.info("User '{}' remove like from film '{}'", user.getName(), film.getName());
    }

    public Collection<Film> getListOfPopular(int count) {
        return filmStorage.getPopularFilms(count);
    }
}
