-- V7__create_audit_log_relation.sql

CREATE TABLE audit_log (
                           id UUID PRIMARY KEY

                           user_id UUID NULL,

                           action VARCHAR(50) NOT NULL,

                           ip_address VARCHAR(45),

                           success BOOLEAN NOT NULL,

                           failure_reason VARCHAR(255),

                           metadata VARCHAR(500),

                           timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);