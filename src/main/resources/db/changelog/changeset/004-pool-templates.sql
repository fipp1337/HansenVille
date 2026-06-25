--liquibase formatted sql

--changeset fipp1337:4

CREATE TABLE pool_templates
(
    id          BIGSERIAL PRIMARY KEY,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    day_of_week INT  NOT NULL,
    max_capacity INT
);
