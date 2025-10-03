create table comment
(
    isAReply            boolean      not null,
    created_at          timestamp(6) not null,
    profile_id          bigint,
    content_entity_id   uuid         not null,
    id                  uuid         not null,
    original_comment_id uuid,
    content             varchar(255) not null,
    primary key (id)
);

create table content_entity
(
    id uuid not null,
    primary key (id)
);

create table job_skill
(
    profile_id bigint       not null,
    id         uuid         not null,
    experience varchar(255) check (experience in
                                   ('NONE', 'ONE_PLUS', 'TWO_PLUS', 'THREE_PLUS', 'FOUR_PLUS', 'FIVE_PLUS')),
    job_title  varchar(255) not null,
    primary key (id)
);

create table language_skill
(
    profile_id           bigint       not null,
    id                   uuid         not null,
    language_code        varchar(255) not null,
    language_proficiency varchar(255) check (language_proficiency in ('A1', 'A2', 'B1', 'B2', 'C1', 'C2')),
    primary key (id)
);

create table likes
(
    created_at        timestamp(6),
    profile_id        bigint not null,
    content_entity_id uuid   not null,
    primary key (profile_id, content_entity_id)
);

create table post
(
    likes_amount     integer      not null,
    reposted         boolean      not null,
    created_at       timestamp(6) not null,
    profile_id       bigint,
    id               uuid         not null,
    original_post_id uuid,
    text_content     varchar(255),
    primary key (id)
);

create table post_preference
(
    profile_id bigint unique,
    id         uuid not null,
    "exclude" text,
    liked      text,
    primary key (id)
);

create table profile
(
    employee_rating     float(53),
    id                  bigint       not null,
    banner_picture_url  varchar(255),
    current_occupation  varchar(255),
    display_name        varchar(255) not null,
    profile_picture_url varchar(255),
    status              varchar(255) check (status in ('NEWBIE', 'EMPLOYEE', 'EMPLOYER', 'COMPANY', 'HIRED')),
    tag                 varchar(255) not null unique,
    bio_markdown        text,
    primary key (id)
);