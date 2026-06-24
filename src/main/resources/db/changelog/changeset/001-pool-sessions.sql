--liquibase formatted sql

--changeset fipp1337:1

CREATE TABLE pool_sessions
(
    id           BIGSERIAL PRIMARY KEY,
    start_time   TIME        NOT NULL,
    end_time     TIME        NOT NULL,
    max_capacity INT DEFAULT 40,
    status       VARCHAR(50) NOT NULL,
    session_date DATE        NOT NULL,
    booked_count INT DEFAULT 0,
    day_of_week  INT         NOT NULL,
    is_exclusive BOOLEAN DEFAULT FALSE
);

