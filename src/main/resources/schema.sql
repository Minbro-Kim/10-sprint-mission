CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    bytes        BYTEA        NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL
);

/*
 파일고도화에 따라 바이트 열 삭제 필요
 */
ALTER TABLE binary_contents
    DROP COLUMN bytes;

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID UNIQUE,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_profile_id
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL
);



CREATE TABLE user_statuses
(
    id             UUID PRIMARY KEY,
    user_id        UUID UNIQUE NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    last_active_at TIMESTAMPTZ NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ
);

CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE')),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ
);

CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id   UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    last_read_at TIMESTAMPTZ NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ,
    CONSTRAINT user_channel_id UNIQUE (user_id, channel_id)
);

CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    content    TEXT,
    author_id  UUID        REFERENCES users (id) ON DELETE SET NULL,
    channel_id UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);

CREATE TABLE message_attachments
(
    message_id    UUID NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id UUID NOT NULL REFERENCES binary_contents (id) ON DELETE CASCADE
);
