package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(max = 200, message = "Длина строки описания фильма должна быть не более 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;

    private Set<Long> whoLiked = new HashSet<>();
}
