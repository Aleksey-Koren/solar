UPDATE objects
    JOIN planets ON objects.id = planets.id
SET objects.planet = planets.parent;

ALTER TABLE planets
    DROP COLUMN parent;