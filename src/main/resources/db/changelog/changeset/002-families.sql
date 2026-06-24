--liquibase formatted sql

--changeset fipp1337:2

CREATE TABLE families (
    username VARCHAR(40) NOT NULL unique,
    id       BIGSERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL unique,
    address  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       family_id BIGINT NOT NULL REFERENCES families (id),
                       full_name VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);