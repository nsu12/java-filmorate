package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    Integer reviewId;
    @NotNull String content;
    Boolean isPositive;
    Integer userId;
    Integer filmId;
    Integer useful;

}
