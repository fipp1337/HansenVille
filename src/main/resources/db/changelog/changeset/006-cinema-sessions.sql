--liquibase formatted sql

--changeset fipp1337:3

CREATE TABLE cinema_sessions
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    movie_name VARCHAR(255) NOT NULL,
    start_time  TIME NOT NULL,
    duration INT NOT NULL,
    max_capacity INT,
    status VARCHAR(50) NOT NULL,
    session_date DATE        NOT NULL,
    booked_count INT DEFAULT 0,
    version INT DEFAULT 0 NOT NULL
);