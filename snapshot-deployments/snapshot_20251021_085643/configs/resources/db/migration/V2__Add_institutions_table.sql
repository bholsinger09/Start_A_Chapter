-- V2__Add_institutions_table.sql
-- Add institutions table with inheritance support for universities and churches

CREATE TABLE IF NOT EXISTS institutions (
    id BIGSERIAL PRIMARY KEY,
    institution_type VARCHAR(50) NOT NULL, -- 'UNIVERSITY' or 'CHURCH'
    name VARCHAR(150) NOT NULL,
    state VARCHAR(50) NOT NULL,
    city VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20),
    address VARCHAR(200),
    description VARCHAR(500),
    
    -- University-specific fields
    university_type VARCHAR(100), -- 'Public', 'Private', 'Community College'
    accreditation VARCHAR(50),
    student_population INTEGER,
    founded_year INTEGER,
    
    -- Church-specific fields
    denomination VARCHAR(100), -- 'Baptist', 'Methodist', etc.
    pastor_name VARCHAR(100),
    membership_size INTEGER,
    website VARCHAR(200),
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_institutions_name UNIQUE (name)
);

-- Add institution_id to chapters table
ALTER TABLE chapters ADD COLUMN IF NOT EXISTS institution_id BIGINT;

-- Add foreign key constraint
ALTER TABLE chapters ADD CONSTRAINT fk_chapters_institution_id 
    FOREIGN KEY (institution_id) REFERENCES institutions(id);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_institutions_type ON institutions(institution_type);
CREATE INDEX IF NOT EXISTS idx_institutions_state ON institutions(state);
CREATE INDEX IF NOT EXISTS idx_institutions_name ON institutions(name);
CREATE INDEX IF NOT EXISTS idx_chapters_institution_id ON chapters(institution_id);