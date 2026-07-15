--liquibase formatted sql

--changeset fipp1337:8
CREATE TABLE invite_codes (
                              id UUID PRIMARY KEY,
                              code VARCHAR(100) NOT NULL UNIQUE,
                              status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                              email VARCHAR(255)
);

INSERT INTO invite_codes (id, code, status, email)
VALUES (
           'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
           'HANSEN-WELCOME-2026',
           'AVAILABLE',
           NULL
       );