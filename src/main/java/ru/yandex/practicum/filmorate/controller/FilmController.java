package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {
    private final List<Film> films = new ArrayList<>();

    @GetMapping
    public List<Film> getAll() {
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        films.add(film);
        film.setId(films.size());
        log.info("Film {} successfully added", film.getDescription());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

        if (film.getId() > films.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "the film is not found"
            );
        }

        validate(film);
        films.set(film.getId() - 1, film);
        log.info("Film {} successfully updated", film.getDescription());
        return film;
    }

    private void validate(Film film) throws ValidationException {
        final var firstFilmDate = LocalDate.of(1895, 12, 25);
        if (film.getReleaseDate().isBefore(firstFilmDate)) {
            log.error("Film {} is released before 1985-12-25", film.getName());
            throw new ValidationException("Invalid film release date");
        }
    }
}
