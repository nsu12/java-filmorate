package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService service;

    @GetMapping
    public List<Genre> getAll() {
        return service.getAllGenres();
    }

    @GetMapping(value = "/{id}")
    public Genre get(@PathVariable("id") Long genreId) {
        return service.getById(genreId);
    }
}
