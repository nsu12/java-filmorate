package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorStorageImpl implements DirectorStorage{
    @Override
    public Director add(Director director) {
        return null;
    }
}
