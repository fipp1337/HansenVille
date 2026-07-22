--liquibase formatted sql

--changeset fipp1337:11
CREATE TABLE admin_roles (
    admin_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,

    CONSTRAINT fk_admin_roles_admin FOREIGN KEY (admin_id)
    REFERENCES admin_users (id) ON DELETE CASCADE,
    PRIMARY KEY (admin_id, role)
);