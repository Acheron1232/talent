create table comment
(
    isAReply            boolean      not null,
    created_at          timestamp(6) not null,
    id                  uuid         not null,
    original_comment_id uuid,
    post_id             uuid,
    profile_id          uuid,
    content             varchar(255) not null,
    primary key (id)
);

create table job_skill
(
    id         uuid         not null,
    profile_id uuid         not null,
    experience varchar(255) check (experience in
                                   ('NONE', 'ONE_PLUS', 'TWO_PLUS', 'THREE_PLUS', 'FOUR_PLUS', 'FIVE_PLUS')),
    job_title  varchar(255) not null,
    primary key (id)
);

create table language_skill
(
    id                   uuid         not null,
    profile_id           uuid         not null,
    language_code        varchar(255) not null,
    language_proficiency varchar(255) check (language_proficiency in ('A1', 'A2', 'B1', 'B2', 'C1', 'C2')),
    primary key (id)
);

create table likes
(
    created_at timestamp(6),
    post_id    uuid not null,
    profile_id uuid not null,
    primary key (post_id, profile_id)
);

create table post
(
    reposted         boolean,
    created_at       timestamp(6),
    id               uuid not null,
    original_post_id uuid,
    profile_id       uuid not null,
    text_content     varchar(255),
    primary key (id)
);

create table post_preference
(
    id         uuid not null,
    profile_id uuid unique,
    "exclude"  text,
    liked      text,
    primary key (id)
);

create table profile
(
    employee_rating     float(53),
    id                  uuid         not null,
    user_id             bigint       not null unique,
    banner_picture_url  varchar(255),
    current_occupation  varchar(255),
    display_name        varchar(255) not null,
    profile_picture_url varchar(255),
    status              varchar(255) check (status in ('NEWBIE', 'EMPLOYEE', 'EMPLOYER', 'COMPANY', 'HIRED')),
    tag                 varchar(255) not null unique,
    bio_markdown        text,
    primary key (id)
);