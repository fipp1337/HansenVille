--liquibase formatted sql

--changeset lisa:3

CREATE TABLE cinema_halls (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL
);

CREATE TABLE cinema_sessions
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    movie_name VARCHAR(255) NOT NULL,
    poster_image VARCHAR(500),
    start_at TIMESTAMP NOT NULL,
    duration INT NOT NULL,
    description VARCHAR(500) NOT NULL,
    hall_id      UUID       NOT NULL REFERENCES cinema_halls(id),
    status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0
);

-- CREATE INDEX idx_session_movie
-- ON cinema_sessions(movie_name)

CREATE TABLE cinema_seats (
        id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        hall_id      UUID       NOT NULL REFERENCES cinema_halls(id),
        seat_number  VARCHAR(5)        NOT NULL
);

CREATE TABLE cinema_bookings (
   id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   cinema_session_id UUID      NOT NULL REFERENCES cinema_sessions(id),
   seat_id    UUID      NOT NULL REFERENCES cinema_seats(id),
   user_id    UUID      NOT NULL REFERENCES users(id),
   created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
   status VARCHAR(50) NOT NULL,
   UNIQUE (cinema_session_id, seat_id)
);

CREATE INDEX idx_cinema_sessions_start_at
ON cinema_sessions(start_at);

CREATE INDEX idx_cinema_seats_hall_id
ON cinema_seats(hall_id);

-- CREATE INDEX idx_cinema_bookings_cinema_session_id
-- ON cinema_bookings(cinema_session_id);
--
-- CREATE INDEX idx_cinema_bookings_seat_id
--  ON cinema_bookings(seat_id);

CREATE INDEX idx_cinema_bookings_user_id
ON cinema_bookings(user_id);