CREATE TABLE reviews (
                         id BIGSERIAL PRIMARY KEY,

                         memory_item_id BIGINT NOT NULL,

                         reviewed_at TIMESTAMP NOT NULL,

                         result VARCHAR(20) NOT NULL,

                         time_spent_seconds BIGINT NOT NULL,

                         interval_before_review INT NOT NULL,

                         ease_factor_before DOUBLE PRECISION NOT NULL,

                         CONSTRAINT fk_reviews_memory_item
                             FOREIGN KEY (memory_item_id)
                                 REFERENCES memory_items(id)
                                 ON DELETE CASCADE
);