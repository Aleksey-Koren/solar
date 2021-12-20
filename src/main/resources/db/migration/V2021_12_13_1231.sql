ALTER TABLE users
	ADD avatar VARCHAR(400) DEFAULT NULL;

ALTER TABLE objects
    DROP COLUMN speed,
    DROP COLUMN acceleration;

ALTER TABLE objects
    ADD COLUMN speed_x FLOAT,
    ADD COLUMN speed_y FLOAT,
    ADD COLUMN acceleration_x FLOAT,
    ADD COLUMN acceleration_y FLOAT,
    ADD COLUMN rotation_angle FLOAT;