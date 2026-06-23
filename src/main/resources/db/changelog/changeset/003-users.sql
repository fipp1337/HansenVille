--liquibase formatted sql

--changeset fipp1337:3


CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    family_id BIGINT NOT NULL REFERENCES families (id)
);


