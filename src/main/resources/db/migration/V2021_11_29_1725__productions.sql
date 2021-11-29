ALTER TABLE production
    RENAME TO productions;

CREATE TABLE objects_productions
(
    object_id INT NOT NULL REFERENCES objects(id),
    production_id INT NOT NULL REFERENCES productions (id),
    UNIQUE (object_id, production_id)
);

INSERT INTO objects_productions (object_id, production_id)
SELECT station, id FROM productions;

ALTER TABLE productions DROP FOREIGN KEY productions_ibfk_2;

ALTER TABLE productions DROP COLUMN station;
