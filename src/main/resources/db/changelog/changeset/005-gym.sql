--liquibase formatted sql

--changeset lisa:5

/*CREATE TABLE gym_visits
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users (id),
    visit_at   TIMESTAMP   NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gym_group_classes
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title         VARCHAR(255) NOT NULL,
    start_time    TIME         NOT NULL,
    end_time      TIME         NOT NULL,
    trainer_name  VARCHAR(255) NOT NULL,
    trainer_phone VARCHAR(50)  NOT NULL,
    description   VARCHAR(500),

    day_of_week   INT,
    class_date    DATE,

    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gym_closures
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reason       VARCHAR(255),
    closure_date DATE,
    day_of_week  INT,
    start_time   TIME,
    end_time     TIME
);*/