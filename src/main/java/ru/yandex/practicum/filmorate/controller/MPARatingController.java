package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.service.MPARatingService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping
public class MPARatingController {
    private final MPARatingService service;

    @Autowired
    public MPARatingController(MPARatingService service) {
        this.service = service;
    }

    @GetMapping(value = "/mpa")
    public Collection<MPARating> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/mpa/{id}")
    public MPARating get(@PathVariable("id") Long ratingId) {
        return service.getById(ratingId);
    }
}
