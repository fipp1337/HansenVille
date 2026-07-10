--liquibase formatted sql

--changeset fipp1337:2

CREATE TABLE families (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL unique,
    address  VARCHAR(255) NOT NULL unique,
    member_count INT NOT NULL,
    phone_number VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID NOT NULL REFERENCES families (id),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    age INT NOT NULL
);

CREATE TABLE families_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL
);


