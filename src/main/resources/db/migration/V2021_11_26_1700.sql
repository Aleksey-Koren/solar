UPDATE planets SET parent = parent + 488;

ALTER TABLE objects DROP FOREIGN KEY objects_ibfk_1;

ALTER TABLE objects
    ADD CONSTRAINT objects_ibfk_1
        FOREIGN KEY (planet) REFERENCES planets (id)
            ON UPDATE CASCADE;

UPDATE planets SET id = id + 488;

ALTER TABLE objects DROP FOREIGN KEY objects_ibfk_1;

ALTER TABLE objects
    ADD CONSTRAINT objects_ibfk_1
        FOREIGN KEY (planet) REFERENCES planets (id);