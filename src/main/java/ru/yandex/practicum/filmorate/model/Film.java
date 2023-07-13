package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private MpaRating mpa;
    private Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));

    private Set<Director> directors = new HashSet<>();

    public List<Genre> getGenres() {
        return genres.stream().collect(Collectors.toList());
    }

    public void setGenres(List<Genre> listOfGenres) {
        genres.addAll(listOfGenres);
    }

    public void setDirectors(List<Director> listOfDirectors) {
        directors.addAll(listOfDirectors);
    }

    public void clearDirectors() {
        directors.clear();
    }
}
