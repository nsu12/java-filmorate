package ru.yandex.practicum.filmorate.storage.filmReviewRating;

public interface ReviewRatingStorage {

    void addLikeToFilmReview(Integer reviewId, Integer userId);

    void deleteLikeFromFilmReview(Integer reviewId, Integer userId);

    void addDislikeToFilmReview(Integer reviewId, Integer userId);

    void deleteDislikeFromFilmReview(Integer reviewId, Integer userId);

}
