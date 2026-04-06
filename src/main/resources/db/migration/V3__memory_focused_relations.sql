-- =========================
-- V3: Add full memory item fields, reviews, memory_item_tags, memory_item_links
-- =========================

-- =========================
-- MEMORY ITEMS (update)
-- =========================
ALTER TABLE memory_items
    ADD COLUMN next_review_date TIMESTAMP,
    ADD COLUMN interval INT DEFAULT 1,
    ADD COLUMN ease_factor FLOAT DEFAULT 2.5,
    ADD COLUMN review_count INT DEFAULT 0,
    ADD COLUMN note_id BIGINT;

ALTER TABLE memory_items
    ADD CONSTRAINT fk_memory_items_note
        FOREIGN KEY (note_id)
            REFERENCES notes(id)
            ON DELETE SET NULL;

-- =========================
-- TAGS & MEMORY_ITEM_TAGS
-- =========================
CREATE TABLE memory_item_tags (
                                  memory_item_id BIGINT NOT NULL,
                                  tag_id BIGINT NOT NULL,
                                  PRIMARY KEY (memory_item_id, tag_id),
                                  CONSTRAINT fk_memory_item_tags_memory_item
                                      FOREIGN KEY (memory_item_id)
                                          REFERENCES memory_items(id)
                                          ON DELETE CASCADE,
                                  CONSTRAINT fk_memory_item_tags_tag
                                      FOREIGN KEY (tag_id)
                                          REFERENCES tags(id)
                                          ON DELETE CASCADE
);

-- =========================
-- MEMORY ITEM LINKS (self-referencing many-to-many)
-- =========================
CREATE TABLE memory_item_links (
                                   memory_item_id BIGINT NOT NULL,
                                   related_item_id BIGINT NOT NULL,
                                   PRIMARY KEY (memory_item_id, related_item_id),
                                   CONSTRAINT fk_memory_item_links_memory_item
                                       FOREIGN KEY (memory_item_id)
                                           REFERENCES memory_items(id)
                                           ON DELETE CASCADE,
                                   CONSTRAINT fk_memory_item_links_related_item
                                       FOREIGN KEY (related_item_id)
                                           REFERENCES memory_items(id)
                                           ON DELETE CASCADE
);

-- =========================
-- REVIEWS
-- =========================
CREATE TABLE reviews (
                         id BIGSERIAL PRIMARY KEY,
                         memory_item_id BIGINT NOT NULL,
                         review_date TIMESTAMP NOT NULL,
                         rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                         CONSTRAINT fk_reviews_memory_item
                             FOREIGN KEY (memory_item_id)
                                 REFERENCES memory_items(id)
                                 ON DELETE CASCADE
);