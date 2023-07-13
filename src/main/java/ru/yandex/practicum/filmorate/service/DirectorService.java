package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director create(@Valid Director director) {
        return directorStorage.add(director);
    }

    public Director get(Long id) {
        return directorStorage.getById(id);
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAll();
    }

    public void delete(Long id) {
        directorStorage.delete(id);
    }

    public Director update(@Valid Director director) {
        return directorStorage.update(director);
    }
}
