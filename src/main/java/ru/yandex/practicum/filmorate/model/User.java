package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    @Email
    private String email;
    @Pattern(regexp = "\\S+", message = "Логин пользователя не должен содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения пользователя должна быть в прошлом")
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();
}
