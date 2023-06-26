package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.mpa.RatingStorage;

import java.util.Collection;

@Service
public class MPARatingService {
    private final RatingStorage ratingStorage;

    @Autowired
    public MPARatingService(RatingStorage storage) {
        this.ratingStorage = storage;
    }

    public MPARating getById(long id) {
        return ratingStorage.getOrThrow(id);
    }

    public Collection<MPARating> getAll() {
        return ratingStorage.getAll();
    }
}
