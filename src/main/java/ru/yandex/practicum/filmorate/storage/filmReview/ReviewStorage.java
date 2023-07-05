package ru.yandex.practicum.filmorate.storage.filmReview;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review add(Review filmReview);

    Review update(Review filmReview);

    void delete(Integer id);

    Review getById(Integer id);

    List<Review> getByFilmId(Integer id);

    List<Review> getAll();

    boolean isExists(Integer id);

}
