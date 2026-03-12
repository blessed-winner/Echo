CREATE TABLE users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(255) NOT NULL
);

CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(36),
    CONSTRAINT fk_topics_on_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- Minimal table for MemoryItem, as the entity definition was not provided in the context.
-- You should expand this based on your MemoryItem.java entity.
CREATE TABLE memory_items (
    id BIGSERIAL PRIMARY KEY,
    -- TODO: Add other columns for your MemoryItem entity here (e.g., content, type).
    user_id VARCHAR(36),
    CONSTRAINT fk_memory_items_on_user FOREIGN KEY (user_id) REFERENCES users (id)
);