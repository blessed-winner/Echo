-- =========================
-- ADD user_id TO TAGS
-- =========================

-- 1. Add column (initially nullable to avoid breaking existing data)
ALTER TABLE tags
    ADD COLUMN user_id UUID;

ALTER TABLE tags
    ALTER COLUMN user_id SET NOT NULL;

-- 4. Add foreign key constraint
ALTER TABLE tags
    ADD CONSTRAINT fk_tags_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;