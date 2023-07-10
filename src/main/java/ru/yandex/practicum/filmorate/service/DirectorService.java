package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director create(Director director) {
        Director directorCreated = directorStorage.add(director);
        return directorCreated;
    }

    public Director get(Long id) {
        return directorStorage.getById(id);
    }

    public Collection<Director> getAllDirectors() {
        return directorStorage.getAll();
    }

    public void delete(Long id) {
        directorStorage.delete(id);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }
}
