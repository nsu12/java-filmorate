package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping(value = "/films")
    public Collection<Film> getAll() {
        return service.getAllFilms();
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
       return service.create(film);
    }

    @GetMapping(value = "/films/{id}")
    public Film get(@PathVariable("id") Long filmId) {
        return service.getById(filmId);
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        return service.update(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void setLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        service.addLikeFromUser(filmId, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        service.removeLikeFromUser(filmId, userId);
    }

    @GetMapping(value = "/films/popular")
    public Collection<Film> getListOfFirstPopularFilms(
            @RequestParam(name = "count", defaultValue = "10") Integer count
    ) {
        return service.getListOfPopular(count);
    }
}
