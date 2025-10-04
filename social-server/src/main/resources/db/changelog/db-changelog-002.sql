CREATE TABLE short (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       profile_id bigint NOT NULL references profile(id),
                       description TEXT,
                       type VARCHAR(20) NOT NULL,
                       views BIGINT DEFAULT 0,
                       is_public BOOLEAN DEFAULT True,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

CREATE TABLE short_element (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               short_id UUID NOT NULL,
                               type VARCHAR(20) NOT NULL,
                               url VARCHAR(512) NOT NULL,
                               order_index INT,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_short FOREIGN KEY (short_id) REFERENCES short(id)
);

CREATE TABLE tag (
                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                     name VARCHAR(100) UNIQUE NOT NULL,
                     description TEXT
);

CREATE TABLE short_tag (
                           short_id UUID NOT NULL,
                           tag_id UUID NOT NULL,
                           PRIMARY KEY (short_id, tag_id),
                           CONSTRAINT fk_short FOREIGN KEY (short_id) REFERENCES short(id),
                           CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tag(id)
);