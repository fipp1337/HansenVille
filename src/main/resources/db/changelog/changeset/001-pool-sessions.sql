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
    version INT DEFAULT 0 NOT NULL
);

CREATE INDEX idx_pool_sessions_session_date
ON pool_sessions(session_date);

CREATE INDEX idx_pool_sessions_session_date_start_time
ON pool_sessions(session_date, start_time);