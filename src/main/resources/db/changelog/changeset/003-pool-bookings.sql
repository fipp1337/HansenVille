--liquibase formatted sql

--changeset fipp1337:3

CREATE TABLE pool_bookings
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users (id),
    pool_session_id UUID NOT NULL REFERENCES pool_sessions (id),
    status VARCHAR
);

CREATE INDEX idx_pool_bookings_user_id_pool_session_id
ON pool_bookings(user_id, pool_session_id);

CREATE INDEX idx_pool_bookings_pool_session_id
ON pool_bookings(pool_session_id);