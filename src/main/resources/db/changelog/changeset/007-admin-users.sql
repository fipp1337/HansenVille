--liquibase formatted sql

--changeset fipp1337:7

CREATE TABLE admin_users
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100)
);