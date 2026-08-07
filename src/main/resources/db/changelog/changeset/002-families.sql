--liquibase formatted sql

--changeset fipp1337:2

CREATE TABLE families (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    address  VARCHAR(255) NOT NULL,
    profile_picture VARCHAR(500),
    member_count INT NOT NULL,
    phone_number VARCHAR(60),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_families_address
ON families(address);

CREATE INDEX idx_families_email
ON families(email);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID NOT NULL REFERENCES families (id),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    age INT NOT NULL
);

CREATE INDEX idx_users_family_id
ON users(family_id);

CREATE TABLE families_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL
);