package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

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
    void deleteOrThrow(long id);

    /**
     * Возвращает список заданного количества самых популярных фильмов,
     * отсортированных по популярности (количеству лайков)
     * @param count количество фильмов для вывода
     * @return список фильмов
     */
    Collection<Film> getPopularFilms(int count);

   /**
     * Возвращает список рекомендованных пользователем фильмов
     * @param userId идентификатор пользователя
     * @return список фильмов
     */
    Collection<Film> getRecommendedFilms(long userId);

   /**
     * Возвращает список общих для обоих пользователей фильмов, отсортированных по популярности (количеству лайков)
     * @param user1Id идентификатор пользователя 1
     * @param user2Id идентификатор пользователя 2
     * @return список фильмов
     */
    Collection<Film> getCommonFilmsSortedByPopularity(long user1Id, long user2Id);

    List<Film> findFilmsByGenre(Long genreId);
}
