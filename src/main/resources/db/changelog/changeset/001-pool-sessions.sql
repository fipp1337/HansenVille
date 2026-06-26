--liquibase formatted sql

--changeset fipp1337:1

CREATE TABLE pool_sessions
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time   TIME        NOT NULL,
    end_time     TIME        NOT NULL,
    max_capacity INT DEFAULT 40,
    status       VARCHAR(50) NOT NULL,
    session_date DATE        NOT NULL,
    booked_count INT DEFAULT 0,
    day_of_week  INT         NOT NULL
);

