CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE topics (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(150) NOT NULL,
                        description TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                        user_id BIGINT NOT NULL,

                        CONSTRAINT fk_topics_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id)
                                ON DELETE CASCADE
);

CREATE TABLE notes (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       content TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       topic_id BIGINT NOT NULL,

                       CONSTRAINT fk_notes_topic
                           FOREIGN KEY (topic_id)
                               REFERENCES topics(id)
                               ON DELETE CASCADE
);


CREATE TABLE tags (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(100) UNIQUE NOT NULL
);

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

CREATE TABLE memory_items (
                              id BIGSERIAL PRIMARY KEY,
                              text TEXT NOT NULL,
                              source VARCHAR(150),
                              mastery_level INT DEFAULT 0,
                              last_reviewed TIMESTAMP,

                              user_id BIGINT NOT NULL,

                              CONSTRAINT fk_memory_items_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE
);