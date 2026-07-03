--liquibase formatted sql

--changeset fipp1337:3

/*
CREATE TABLE gym_templates
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    day_of_week INT  NOT NULL
);
  */