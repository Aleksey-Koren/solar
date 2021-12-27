ALTER TABLE object_type_description MODiFY sub_type enum('STATIC', 'MINING', 'MILITARY', 'SCIENCE', 'PRODUCTION', 'ASYLUM', 'DYNAMIC') NULL;

UPDATE object_type_description
SET sub_type = 'STATIC'
WHERE (description LIKE '%immobile%' AND type = 'STATION') OR title = 'Planet';

UPDATE object_type_description
SET sub_type = 'DYNAMIC'
WHERE sub_type IS NULL;

ALTER TABLE courses
    ADD COLUMN duration BIGINT NULL,
    DROP COLUMN x,
    DROP COLUMN y;