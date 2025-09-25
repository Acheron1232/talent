--liquibase formatted sql

--changeset artem:1
CREATE TABLE mail
(
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT,
    from_email    VARCHAR(128) NOT NULL,
    to_email      VARCHAR(128) NOT NULL,
    subject      VARCHAR(128) NOT NULL,
    content TEXT
);
