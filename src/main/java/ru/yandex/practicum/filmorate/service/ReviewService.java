package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntryNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmReview.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.filmReviewRating.ReviewRatingStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewRatingStorage reviewLikeStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final EventService eventService;

    public Review getById(Integer id) {
        log.debug("Запрошен отзыв с id = {}", id);
        return checkIfReviewExists(id);
    }

    public List<Review> getAll(Integer filmId, Integer count) {
        List<Review> reviews;
        if (filmId == -1) {
            log.debug("Запрошены все отзывы");
            reviews = reviewStorage.getAll();
        } else {
            log.debug("Запрошены все отзывы фильма с id = {}", filmId);
            reviews = reviewStorage.getByFilmId(filmId);
        }
        log.debug("Количество выгруженных отзывов: {}", reviews.size());
        log.trace("Перечень отзывов: {}", reviews.stream().map(Review::toString));
        if (reviews.size() > count) {
            reviews = reviews.stream().limit(count).collect(Collectors.toList());
            log.debug("Количество отзывов ограничено {}", count);
            log.trace("Итоговый перечень отзывов: {}", reviews.stream().map(Review::toString));
        }
        return reviews;
    }

    public Review add(Review filmReview) {
        log.debug("Добавление нового отзыва: {}", filmReview);
        performChecks(filmReview);
        Review review = reviewStorage.add(filmReview);
        log.debug("Добавлен отзыв с id = {}", review.getReviewId());
        log.trace("Итоговый отзыв: {}", review);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.ADD, review.getReviewId());
        return review;
    }

    public Review update(Review filmReview) {
        log.debug("Обновление отзыва: {}", filmReview);
        performChecks(filmReview);
        Review review = reviewStorage.update(filmReview);
        log.debug("Отзыв обновлён");
        log.trace("Итоговый отзыв: {}", review);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.UPDATE, review.getReviewId());
        return review;
    }

    public void delete(Integer id) {
        log.debug("Удаление отзыва с id = {}", id);
        Review review = checkIfReviewExists(id);
        reviewStorage.delete(id);
        log.debug("Удалён отзыв с id = {}", id);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, review.getReviewId());
    }

    public void addLikeToFilmReview(Integer reviewId, Integer userId) {
        log.debug("Добавление лайка отзыву с id = {} от пользователя с id = {}", reviewId, userId);
        changeUserReviewReaction(reviewId, userId, true);
        log.debug("Лайк добавлен");
        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, reviewId);
    }

    public void addDislikeToFilmReview(Integer reviewId, Integer userId) {
        log.debug("Добавление дизлайка отзыву с id = {} от пользователя с id = {}", reviewId, userId);
        changeUserReviewReaction(reviewId, userId, false);
        log.debug("Дизлайк добавлен");
        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, reviewId);
    }

    public void deleteLikeFromFilmReview(Integer reviewId, Integer userId) {
        log.debug("Удаление лайка к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
        clearUserReviewReaction(reviewId, userId, true);
        log.debug("Выполнено");
        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, reviewId);
    }

    public void deleteDislikeFromFilmReview(Integer reviewId, Integer userId) {
        log.debug("Удаление дизлайка к отзыву с id = {} от пользователя с id = {}", reviewId, userId);
        clearUserReviewReaction(reviewId, userId, false);
        log.debug("Выполнено");
        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, reviewId);
    }

    private void changeUserReviewReaction(Integer reviewId, Integer userId, boolean isPositive) {
        User user = userService.getUserById(userId);
        checkIfReviewExists(reviewId);
        if (isPositive) {
            reviewLikeStorage.addLikeToFilmReview(reviewId, (int) user.getId());
        } else {
            reviewLikeStorage.addDislikeToFilmReview(reviewId, (int) user.getId());
        }
    }

    private void clearUserReviewReaction(Integer reviewId, Integer userId, boolean isLike) {
        User user = userService.getUserById(userId);
        checkIfReviewExists(reviewId);
        if (isLike) {
            reviewLikeStorage.deleteLikeFromFilmReview(reviewId, (int) user.getId());
        } else {
            reviewLikeStorage.deleteDislikeFromFilmReview(reviewId, (int) user.getId());
        }
    }

    private void performChecks(Review filmReview) {
        log.debug("Запуск проверок отзыва");
        if (filmReview.getContent() == null || filmReview.getContent().isBlank()) {
            log.warn("Ошибка проверки значений текста отзыва");
            throw new ReviewValidationException("Текст отзыва заполнен некорректно");
        }
        if (filmReview.getUserId() == null || userService.getUserById(filmReview.getUserId()) == null) {
            log.warn("Не обнаружен пользователь с id = {}", filmReview.getUserId());
            throw new ReviewValidationException("Данные о пользователе заполнены некорректно");
        }
        if (filmReview.getFilmId() == null || filmService.getById(filmReview.getFilmId()) == null) {
            log.warn("Не обнаружен фильм с id = {}", filmReview.getFilmId());
            throw new ReviewValidationException("Данные о фильме заполнены некорректно");
        }
        if (filmReview.getIsPositive() == null) {
            log.warn("Ошибка проверки значения типа отзыва");
            throw new ReviewValidationException("Данные о типе отзыва заполнены некорректно");
        }
        filmReview.setUseful(0);
        log.debug("Проверки пройдены успешно");
    }

    private Review checkIfReviewExists(Integer reviewId) {
        return Optional.ofNullable(reviewStorage.getById(reviewId))
                .orElseThrow(() -> new EntryNotFoundException("Отзыв не обнаружен"));
    }
}
