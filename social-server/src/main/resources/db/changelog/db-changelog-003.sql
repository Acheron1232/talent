create table follow (
    follower_id bigint references profile(id),
    followed_id bigint references profile(id),
    created_at        timestamp(6),
    primary key (follower_id, followed_id)
);