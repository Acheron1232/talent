CREATE TABLE post_element
(
    id          UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    post_id     UUID         NOT NULL references post (id),
    type        VARCHAR(20)  NOT NULL CHECK ( type in ('VIDEO', 'MUSIC', 'VIDEO') ),
    url         VARCHAR(512) NOT NULL,
    order_index INT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);