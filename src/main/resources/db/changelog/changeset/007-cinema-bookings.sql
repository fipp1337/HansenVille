--liquibase formatted sql

--changeset fipp1337:3

CREATE TABLE cinema_bookings
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users (id),
    cinema_session_id UUID NOT NULL REFERENCES cinema_sessions (id)
);