--liquibase formatted sql

--changeset fipp1337:9
CREATE TABLE facilities (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    image VARCHAR(255),
    target_route VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO facilities (id, code, name, description, target_route, active)
VALUES
    (gen_random_uuid(), 'POOL', 'Pool', 'Pool sessions', '/pool/booking', true),
    (gen_random_uuid(), 'CINEMA', 'Cinema', 'Cinema sessions', '/cinema/booking', true),
    (gen_random_uuid(), 'GYM', 'Gym', 'Gym sessions', '/gym/booking', true),
    (gen_random_uuid(), 'ACTIVITIES', 'Activities', 'Hansen events', '/activities', true);