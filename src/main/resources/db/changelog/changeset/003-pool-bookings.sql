--liquibase formatted sql

--changeset fipp1337:3

CREATE TABLE pool_bookings
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users (id),
    pool_session_id UUID NOT NULL REFERENCES pool_sessions (id),
    status VARCHAR
);
