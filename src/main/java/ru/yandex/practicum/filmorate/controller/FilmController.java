package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Film> getAll() {
        return service.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
       return service.create(film);
    }

    @GetMapping(value = "/{id}")
    public Film get(@PathVariable("id") Long filmId) {
        return service.getById(filmId);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return service.update(film);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long filmId) {
        service.delete(filmId);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void setLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        service.addLikeFromUser(filmId, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        service.removeLikeFromUser(filmId, userId);
    }

    @GetMapping(value = "/popular")
    public Collection<Film> getListOfFirstPopularFilms(
            @RequestParam(name = "count", defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Long genreId,
            @RequestParam(defaultValue = "0") Integer year
    ) {
        return service.getListOfPopular(count, genreId, year);
    }

    @GetMapping(value = "/director/{id}")
    public Collection<Film> getFilmsWithDirectorSortedByYear(
            @PathVariable("id") Long id,
            @RequestParam(name = "sortBy") String sortBy
    ) {
        return service.getListFilmOfDirectorSortedBy(id, sortBy);
    }

    @GetMapping(value = "/common")
    public Collection<Film> getListOfCommonFilms(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "friendId") Long friendId
    ) {
        return service.getListOfCommon(userId, friendId);
    }

    @GetMapping(value = "/search")
    protected Collection<Film> searchFilms(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "by") List<String> by) {
        log.debug("Запрос на поиск фильмов...");
        return service.searchFilms(query, by);
    }
}
