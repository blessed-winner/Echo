-- Step 1: Add column with default
ALTER TABLE users
    ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';

-- Step 2 (optional but recommended): add constraint for allowed values
ALTER TABLE users
    ADD CONSTRAINT chk_user_role
        CHECK (role IN ('USER', 'ADMIN'));