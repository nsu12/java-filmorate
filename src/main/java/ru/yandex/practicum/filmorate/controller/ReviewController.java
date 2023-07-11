package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/reviews")
public class ReviewController {

    ReviewService reviewService;
    EventService eventService;

    @PostMapping
    public Review add(@Valid @RequestBody Review filmReview) {
        return reviewService.add(filmReview);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review filmReview) {
        return reviewService.update(filmReview);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        reviewService.delete(id);
    }

    @GetMapping
    public List<Review> getAll(
            @RequestParam(name = "filmId", defaultValue = "-1", required = false) Integer filmId,
            @RequestParam(name = "count", defaultValue = "10", required = false) Integer count
    ) {
        return reviewService.getAll(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable("id") Integer id) {
        return reviewService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addUserLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        reviewService.addLikeToFilmReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addUserDislike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        reviewService.addDislikeToFilmReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteUserLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        reviewService.deleteLikeFromFilmReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteUserDislike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        reviewService.deleteDislikeFromFilmReview(id, userId);
    }

}
