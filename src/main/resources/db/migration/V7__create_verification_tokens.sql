-- V1__create_verification_tokens.sql

CREATE TABLE verification_tokens (
                                     id UUID PRIMARY KEY,
                                     user_id UUID NOT NULL,
                                     token_hash VARCHAR(64) NOT NULL UNIQUE,
                                     type VARCHAR(50) NOT NULL,
                                     expires_at TIMESTAMP NOT NULL,
                                     used_at TIMESTAMP NULL,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_token_hash ON verification_tokens(token_hash);
CREATE INDEX idx_user_type ON verification_tokens(user_id, type);
CREATE INDEX idx_expires_at ON verification_tokens(expires_at);

-- Optional: Foreign key (recommended if you have users table)
ALTER TABLE verification_tokens
    ADD CONSTRAINT fk_verification_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;