ALTER TABLE objects
    ADD CONSTRAINT objects_ibfk_4
        FOREIGN KEY (attached_to_ship) REFERENCES objects (id);