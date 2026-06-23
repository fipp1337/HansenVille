--liquibase formatted sql

--changeset fipp1337:4

CREATE TABLE pool_bookings
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users (id),
    pool_session BIGINT NOT NULL REFERENCES pool_sessions (id)
);