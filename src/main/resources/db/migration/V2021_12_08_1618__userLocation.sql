ALTER TABLE users
    DROP CONSTRAINT user_planet_fk;

ALTER TABLE users
    DROP INDEX user_planet_fk_idx;

ALTER TABLE users CHANGE planet location INT;

ALTER TABLE users
    ADD CONSTRAINT users_objects_id_fk
        FOREIGN KEY (location) REFERENCES objects (id);