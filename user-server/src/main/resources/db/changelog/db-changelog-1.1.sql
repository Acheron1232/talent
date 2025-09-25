-- --liquibase formatted sql

--changeset artem:1
create table token(
    id bigserial primary key ,
    token text not null ,
    user_id bigint references users(id),
    expired_at timestamp not null,
    token_status text not null ,
    token_type text not null
);