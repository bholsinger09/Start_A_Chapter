-- V3__Migrate_existing_chapters_to_institutions.sql
-- Migrate existing chapter data to use institutions

-- Insert universities based on existing chapter data
INSERT INTO institutions (institution_type, name, state, city, founded_year, description, created_at, updated_at)
SELECT DISTINCT 
    'UNIVERSITY' as institution_type,
    university_name as name,
    state,
    city,
    EXTRACT(YEAR FROM founded_date) as founded_year,
    'Migrated from existing chapter data' as description,
    CURRENT_TIMESTAMP as created_at,
    CURRENT_TIMESTAMP as updated_at
FROM chapters 
WHERE university_name IS NOT NULL 
  AND NOT EXISTS (
    SELECT 1 FROM institutions i 
    WHERE i.name = chapters.university_name
  );

-- Update chapters to reference the corresponding institutions using subquery
UPDATE chapters 
SET institution_id = (
    SELECT i.id 
    FROM institutions i 
    WHERE i.name = chapters.university_name
      AND i.state = chapters.state
      AND i.city = chapters.city
    LIMIT 1
)
WHERE chapters.university_name IS NOT NULL 
  AND chapters.institution_id IS NULL
  AND EXISTS (
    SELECT 1 
    FROM institutions i 
    WHERE i.name = chapters.university_name
      AND i.state = chapters.state
      AND i.city = chapters.city
  );