DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS review_ratings;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS favorite_films;
DROP TABLE IF EXISTS film CASCADE;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS mpa_rating;
DROP TABLE IF EXISTS review_ratings;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS user_friends;
DROP TABLE IF EXISTS user_account CASCADE;
DROP TABLE IF EXISTS director_film;
DROP TABLE IF EXISTS director;

CREATE TABLE IF NOT EXISTS mpa_rating (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS genre (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS director (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS film (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(200),
    release_date TIMESTAMP,
    duration INTEGER,
    rating_id INTEGER REFERENCES mpa_rating(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS director_film(
   film_id INTEGER REFERENCES film(id) ON DELETE CASCADE,
   director_id INTEGER REFERENCES director(id) ON DELETE CASCADE,
   CONSTRAINT uniq_film_director_pair UNIQUE (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INTEGER REFERENCES film(id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre(id) ON DELETE CASCADE,
    CONSTRAINT uniq_film_genre_pair UNIQUE (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS user_account (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login VARCHAR(64) NOT NULL,
    name VARCHAR(64),
    email VARCHAR(64),
    birthday TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_friends (
    user_id INTEGER REFERENCES user_account(id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES user_account(id) ON DELETE CASCADE,
    confirmed BOOLEAN,
    CONSTRAINT uniq_user_friend_pair UNIQUE (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS favorite_films (
    user_id INTEGER REFERENCES user_account(id) ON DELETE CASCADE,
    film_id INTEGER REFERENCES film(id) ON DELETE CASCADE,
    CONSTRAINT uniq_user_film_pair UNIQUE (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id  INTEGER,
    user_id INTEGER,
    content VARCHAR(64),
    is_positive BOOLEAN,
    useful INTEGER,
    FOREIGN KEY (film_id) REFERENCES film (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user_account (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review_ratings (
    review_id INTEGER,
    user_id INTEGER,
    is_positive BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, review_id),
    FOREIGN KEY (user_id) REFERENCES user_account (id) ON DELETE CASCADE,
    FOREIGN KEY (review_id) REFERENCES reviews (review_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event (
    event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id INTEGER,
    event_type VARCHAR,
    operation VARCHAR,
    ts BIGINT,
    entity_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES user_account (id) ON DELETE CASCADE
);

