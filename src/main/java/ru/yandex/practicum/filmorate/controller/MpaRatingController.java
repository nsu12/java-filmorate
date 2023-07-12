package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/mpa")
@RequiredArgsConstructor
public class MpaRatingController {
    private final MpaRatingService service;

    @GetMapping
    public List<MpaRating> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}")
    public MpaRating get(@PathVariable("id") Long ratingId) {
        return service.getById(ratingId);
    }
}
