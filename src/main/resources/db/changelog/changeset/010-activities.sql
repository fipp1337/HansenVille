--liquibase formatted sql

--changeset fipp1337:10
CREATE TABLE activities (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(150) NOT NULL,
    image VARCHAR(255),
    type VARCHAR(20),
    family_id UUID NOT NULL REFERENCES families (id),
    phone_number VARCHAR(25) NOT NULL,
    day_of_week smallint[],
    date_time TIMESTAMP,
    location VARCHAR(100)
);