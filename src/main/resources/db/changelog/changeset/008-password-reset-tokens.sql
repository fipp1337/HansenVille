--liquibase formatted sql

--changeset fipp1337:7

-- CREATE TABLE password_reset_tokens(
--       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
--       expiryDate TIMESTAMP NOT NULL,
--       user_id UUID NOT NULL REFERENCES users(id)
-- );