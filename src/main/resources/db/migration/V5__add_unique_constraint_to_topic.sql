ALTER TABLE topics
    ADD CONSTRAINT uk_topic_name_user UNIQUE (name, user_id);