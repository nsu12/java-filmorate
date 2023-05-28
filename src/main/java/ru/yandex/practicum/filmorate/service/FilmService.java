package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
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

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() { return filmStorage.getAll(); }

    public Film create(@Valid Film film) {
        validateReleaseDate(film);
        return filmStorage.addOrThrow(film);
    }

    public Film getById(long id) {
        return filmStorage.getOrThrow(id);
    }

    public Film update(@Valid Film film) {
        validateReleaseDate(film);
        filmStorage.updateOrThrow(film);
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
        if (film.getWhoLiked().add(userId)) {
            filmStorage.updateOrThrow(film);
            log.info("Film '{}' liked by user '{}'", film.getName(), user.getLogin());
        }
    }

    public void removeLikeFromUser(long filmId, long userId) {
        final Film film = filmStorage.getOrThrow(filmId);
        final User user = userStorage.getOrThrow(userId);
        if (film.getWhoLiked().remove(userId)) {
            filmStorage.updateOrThrow(film);
            log.info("User '{}' remove like from film '{}'", user.getName(), film.getName());
        }
    }

    public Collection<Film> getListOfPopular(int count) {
        return filmStorage.getAll().stream()
                .sorted(this::compareByLikes)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compareByLikes(final Film f0, final Film f1) {
        return -1*Integer.compare(f0.getWhoLiked().size(), f1.getWhoLiked().size()); // sort from more to less (inverse order)
    }
}
