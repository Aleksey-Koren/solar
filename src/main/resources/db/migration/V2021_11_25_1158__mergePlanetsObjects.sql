AlTER TABLE objects
    ADD COLUMN acceleration FLOAT DEFAULT NULL;

AlTER TABLE objects
    ADD COLUMN speed FLOAT DEFAULT NULL;

INSERT INTO object_types (title)
VALUES
    ('planet');