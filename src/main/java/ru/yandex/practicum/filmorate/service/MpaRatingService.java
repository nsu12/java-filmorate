package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingStorage ratingStorage;

    public MpaRating getById(long id) {
        return ratingStorage.getOrThrow(id);
    }

    public List<MpaRating> getAll() {
        return ratingStorage.getAll();
    }
}
