package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class FilmService {

    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public Collection<Film> getAllFilms() { return storage.getAll(); }

    public Film create(@Valid Film film) {
        validateReleaseDate(film);
        return storage.add(film);
    }

    public Film getById(long id) {
        return storage.get(id);
    }

    public Film update(@Valid Film film) {
        validateReleaseDate(film);
        storage.update(film);
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
        final Film film = storage.get(filmId);
        if (film.getWhoLiked().add(userId)) {
            storage.update(film);
            log.info("Film '{}' liked by user with id {}", film.getName(), userId);
        }
    }

    public void removeLikeFromUser(long filmId, long userId) {
        final Film film = storage.get(filmId);
        if (film.getWhoLiked().remove(userId)) {
            storage.update(film);
            log.info("User with id {} remove like from film '{}'", userId, film.getName());
        }
    }

    public Collection<Film> getListOfPopular(int count) {
        return storage.getAll().stream()
                .sorted(this::compareByLikes)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compareByLikes(final Film f0, final Film f1) {
        return Integer.compare(f0.getWhoLiked().size(), f1.getWhoLiked().size());
    }
}
