package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping

public class DirectorController {
    private final DirectorService service;

    public DirectorController(DirectorService service) {
        this.service = service;
    }

    @PostMapping(value = "/directors")
    public Director create(@RequestBody @Valid Director director) {
        return service.create(director);
    }
    @PutMapping(value = "/directors")
    public Director update(@RequestBody @Valid Director director) {
        return service.update(director);
    }

    @GetMapping(value = "/directors/{id}")
    public Director get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @GetMapping(value = "/directors")
    public Collection<Director> get() {
        return service.getAllDirectors();
    }

    @DeleteMapping(value = "/directors/{id}")
    public void delete(@PathVariable("id") Long id) {
         service.delete(id);
    }
}
