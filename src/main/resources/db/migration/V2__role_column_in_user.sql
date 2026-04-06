-- Step 1: Add column with default
ALTER TABLE users
    ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';

ALTER TABLE users
    ADD CONSTRAINT chk_user_role
        CHECK (role IN ('USER', 'ADMIN'));