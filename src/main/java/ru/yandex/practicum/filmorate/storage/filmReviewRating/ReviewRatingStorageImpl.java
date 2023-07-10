package ru.yandex.practicum.filmorate.storage.filmReviewRating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("ReviewRatingDbStorage")
public class ReviewRatingStorageImpl implements ReviewRatingStorage {

    private static final String MERGE_REVIEW_RATING_QUERY =
            "MERGE INTO review_ratings (review_id, user_id, is_positive) VALUES (?, ?, ?)";
    private static final String DELETE_REVIEW_RATING_BY_REVIEW_ID_USER_ID =
            "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ?";
    private static final String GET_REVIEW_USEFUL_SCORE_ON_REVIEW_ID =
            "SELECT SUM(CASE WHEN is_positive = TRUE THEN 1 ELSE -1 END) useful FROM review_ratings WHERE review_id = ?";
    private static final String UPDATE_REVIEW_USEFUL_SCORE_ON_USEFUL_SCORE_REVIEW_ID =
            "UPDATE reviews SET useful = ? WHERE review_id = ?";
    private final JdbcTemplate template;
    private final ReviewRatingMapper reviewRatingMapper;

    @Override
    public void addLikeToFilmReview(Integer reviewId, Integer userId) {
        addUserReviewRating(reviewId, userId, Boolean.TRUE);
    }

    @Override
    public void addDislikeToFilmReview(Integer reviewId, Integer userId) {
        addUserReviewRating(reviewId, userId, Boolean.FALSE);
    }

    @Override
    public void deleteLikeFromFilmReview(Integer reviewId, Integer userId) {
        deleteUserReviewRating(reviewId, userId);
    }

    @Override
    public void deleteDislikeFromFilmReview(Integer reviewId, Integer userId) {
        deleteUserReviewRating(reviewId, userId);
    }

    private void addUserReviewRating(Integer reviewId, Integer userId, Boolean isPositive) {
        template.update(MERGE_REVIEW_RATING_QUERY, reviewId, userId, isPositive);
        updateReviewUsefulScore(reviewId);
    }

    private void deleteUserReviewRating(Integer reviewId, Integer userId) {
        template.update(DELETE_REVIEW_RATING_BY_REVIEW_ID_USER_ID, reviewId, userId);
        updateReviewUsefulScore(reviewId);
    }

    private void updateReviewUsefulScore(Integer reviewId) {
        Integer usefulScore = getReviewUsefulScore(reviewId);
        template.update(UPDATE_REVIEW_USEFUL_SCORE_ON_USEFUL_SCORE_REVIEW_ID, usefulScore, reviewId);
    }

    private Integer getReviewUsefulScore(Integer reviewId) {
        return template.query(GET_REVIEW_USEFUL_SCORE_ON_REVIEW_ID, reviewRatingMapper, reviewId)
                .stream()
                .findAny()
                .orElse(null);
    }

}
