-- Add created_at column to memory_items table
ALTER TABLE memory_items ADD COLUMN created_at TIMESTAMP;

-- Set existing rows to current timestamp (or their last_reviewed date if available)
UPDATE memory_items SET created_at = COALESCE(last_reviewed, CURRENT_TIMESTAMP);

-- Make it NOT NULL after setting values
ALTER TABLE memory_items ALTER COLUMN created_at SET NOT NULL;

-- Add default for future inserts
ALTER TABLE memory_items ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
