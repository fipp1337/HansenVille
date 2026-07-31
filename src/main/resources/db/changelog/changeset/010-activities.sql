--liquibase formatted sql

--changeset fipp1337:10
CREATE TABLE activities (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(150) NOT NULL,
    image VARCHAR(255)
);