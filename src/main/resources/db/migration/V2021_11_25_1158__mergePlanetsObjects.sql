AlTER TABLE objects
    ADD COLUMN acceleration FLOAT DEFAULT NULL;

AlTER TABLE objects
    ADD COLUMN speed FLOAT DEFAULT NULL;

INSERT INTO object_types (title)
VALUES
    ('planet');

ALTER TABLE planet ADD COLUMN status ENUM ('in_space', 'attached_to', 'in_container');

UPDATE planet
SET status = 'in_space';


ALTER TABLE planet
    RENAME TO planets;

INSERT INTO objects (aphelion, orbital_period, angle, title, status)
SELECT aphelion, orbital_period, angle, title, status FROM planets;

ALTER TABLE planets
    DROP COLUMN status;