package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class Director {
    private Long id;

    @NotBlank
    private String name;

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
