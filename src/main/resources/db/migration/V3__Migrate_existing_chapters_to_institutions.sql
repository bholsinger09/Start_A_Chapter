-- V3__Migrate_existing_chapters_to_institutions.sql
-- Migrate existing chapter data to use institutions

-- Insert universities based on existing chapter data
INSERT INTO institutions (institution_type, name, state, city, founded_year)
SELECT DISTINCT 
    'UNIVERSITY' as institution_type,
    university_name as name,
    state,
    city,
    EXTRACT(YEAR FROM founded_date) as founded_year
FROM chapters 
WHERE university_name IS NOT NULL 
  AND NOT EXISTS (
    SELECT 1 FROM institutions i 
    WHERE i.name = chapters.university_name
  );

-- Update chapters to reference the corresponding institutions
UPDATE chapters 
SET institution_id = i.id
FROM institutions i
WHERE i.name = chapters.university_name
  AND i.state = chapters.state
  AND i.city = chapters.city
  AND chapters.institution_id IS NULL;