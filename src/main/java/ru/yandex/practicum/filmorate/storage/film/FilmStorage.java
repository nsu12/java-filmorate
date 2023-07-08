package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    /**
     * Добавление фильма в хранилище
     * @param film объект \ref Film который должен быть добавлен
     * @return объект Film обновленными полями в случае успеха, либо исключение EntryAlreadyExistsException
     */
    Film addOrThrow(Film film);

    /**
     * Получение фильма из хранилища
     * @param id фильма
     * @return объект Film, либо исключение в случае если фильм с заданным id не найден
     */
    Film getOrThrow(long id);

    /**
     * Получение списка всех фильмов
     * @return список всех фильмов
     */
    Collection<Film> getAll();

    /**
     * Обновление фильма в хранилице
     * @param film обновляемый объект.
     * Выбрасывает \ref EntryNotFoundException если объект не найден в хранилище
     */
    void updateOrThrow(Film film);

    /**
     * Удаляет фильм из хранилища
     * @param id фильма
     */
    void removeOrThrow(long id);

    Collection<Film> getPopularFilms(int count);

    Collection<Film> getRecommendedFilms(Long userId);
}
