--liquibase formatted sql

--changeset fipp1337:9
CREATE TABLE facilities (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    image VARCHAR(255),
    target_route VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO facilities (id, code, name, description, target_route, active)
VALUES
    (gen_random_uuid(), 'POOL', 'Басейн', 'Відвідуй басейн з родиною 2 рази на тиждень!', '/pool/booking', true),
    (gen_random_uuid(), 'GYM', 'Тренажерний зал', 'Займайся спортом самостійно чи з тренером!', '/gym/booking', true),
    (gen_random_uuid(), 'CINEMA', 'Кінотеатр', 'Переглядай популярне з родиною або друзями!', '/cinema/booking', true),
    (gen_random_uuid(), 'ACTIVITIES', 'Дозвілля', 'Не пропусти найближчі заходи та події в містечку!', '/activities', true);