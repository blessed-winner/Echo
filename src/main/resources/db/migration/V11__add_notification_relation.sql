CREATE TABLE notifications
(
    id UUID PRIMARY KEY,

    recipient_id UUID NOT NULL,

    title VARCHAR(255) NOT NULL,

    message TEXT NOT NULL,

    type VARCHAR(50) NOT NULL,

    status VARCHAR(50) NOT NULL,

    read BOOLEAN NOT NULL DEFAULT FALSE,

    reference_id UUID,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_notification_recipient
        FOREIGN KEY (recipient_id)
            REFERENCES users(id)
);

CREATE INDEX idx_notification_recipient
    ON notifications(recipient_id);

CREATE INDEX idx_notification_recipient_read
    ON notifications(recipient_id, read);

CREATE INDEX idx_notification_created_at
    ON notifications(created_at DESC);