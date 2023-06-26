package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private long id;
    @Pattern(regexp = "\\S+", message = "Логин пользователя не должен содержать пробелы")
    private String login;
    private String name;
    @Email
    private String email;
    @Past(message = "Дата рождения пользователя должна быть в прошлом")
    private LocalDate birthday;
}
