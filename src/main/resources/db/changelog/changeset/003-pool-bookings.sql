--liquibase formatted sql

--changeset fipp1337:3

CREATE TABLE pool_bookings
(
    id           BIGSERIAL PRIMARY KEY,
    member_id      BIGINT NOT NULL REFERENCES users (id),
    pool_session_id BIGINT NOT NULL REFERENCES pool_sessions (id)
);
