-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP
);

-- =========================
-- TOPICS
-- =========================
CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    creation_date TIMESTAMP,

    user_id UUID NOT NULL,

    CONSTRAINT fk_topics_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================
-- NOTES
-- =========================
CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    creation_date TIMESTAMP,

    topic_id BIGINT NOT NULL,

    CONSTRAINT fk_notes_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics(id)
        ON DELETE CASCADE
);

-- =========================
-- TAGS
-- =========================
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- =========================
-- NOTE_TAGS (JOIN TABLE)
-- =========================
CREATE TABLE note_tags (
    note_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    PRIMARY KEY (note_id, tag_id),

    CONSTRAINT fk_note_tags_note
        FOREIGN KEY (note_id)
        REFERENCES notes(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_note_tags_tag
        FOREIGN KEY (tag_id)
        REFERENCES tags(id)
        ON DELETE CASCADE
);

-- =========================
-- MEMORY ITEMS
-- =========================
CREATE TABLE memory_items (
    id BIGSERIAL PRIMARY KEY,
    text VARCHAR(1000) NOT NULL,
    source VARCHAR(150),
    last_reviewed TIMESTAMP,

    user_id UUID NOT NULL,

    CONSTRAINT fk_memory_items_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);