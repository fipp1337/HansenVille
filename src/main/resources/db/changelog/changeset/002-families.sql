--liquibase formatted sql

--changeset fipp1337:2

CREATE TABLE families (

    id       BIGSERIAL PRIMARY KEY,
    quantity int          NOT NULL,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL unique,
    address  VARCHAR(255) NOT NULL
);