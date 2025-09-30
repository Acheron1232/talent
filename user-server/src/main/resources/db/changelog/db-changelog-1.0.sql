--liquibase formatted sql

--changeset artem:1
create table users
(
    id                bigserial primary key,
    email             text unique not null,
    username          text unique not null,
    is_email_verified boolean,
    auth_method       text check (auth_method in ('DEFAULT', 'GOOGLE', 'GITHUB')),
    role              text        not null check (role in ('USER', 'ADMIN')),
    password          text
);