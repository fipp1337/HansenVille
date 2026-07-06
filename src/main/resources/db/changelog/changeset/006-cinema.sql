--liquibase formatted sql

--changeset lisa:3

CREATE TABLE cinema_sessions
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    movie_name VARCHAR(255) NOT NULL,
    start_time  TIME NOT NULL,
    duration INT NOT NULL,
    max_capacity INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    session_date DATE        NOT NULL,
    booked_count INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0
);

CREATE TABLE cinema_halls (
       id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       name VARCHAR(100) NOT NULL DEFAULT 'Домашній зал'
);

CREATE TABLE cinema_seats (
        id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        hall_id      UUID       NOT NULL REFERENCES cinema_halls(id),
        row_label    VARCHAR(5) NOT NULL,
        sofa_number  INT        NOT NULL,
        spot_number  INT        NOT NULL,
        UNIQUE (hall_id, row_label, sofa_number, spot_number)
);

CREATE TABLE cinema_bookings (
       id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       cinema_session_id UUID      NOT NULL REFERENCES cinema_sessions(id),
       seat_id    UUID      NOT NULL REFERENCES cinema_seats(id),
       user_id    UUID      NOT NULL REFERENCES users(id),
--        status     VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
       created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
       UNIQUE (cinema_session_id, seat_id)
);