--liquibase formatted sql

--changeset fipp1337:2

CREATE TABLE families (
    id       BIGSERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL unique,
    address  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    member_count INT
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       family_id BIGINT NOT NULL REFERENCES families (id) ON DELETE CASCADE,
                       name VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       age INT
);
