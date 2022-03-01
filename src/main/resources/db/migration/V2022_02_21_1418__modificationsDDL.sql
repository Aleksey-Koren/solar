CREATE TABLE modifications
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(1000)
);

CREATE TABLE parameter_modifications
(
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    parameter_type     VARCHAR(50) NOT NULL,
    modification_id    INT REFERENCES modifications (id) ON DELETE CASCADE,
    modification_value DOUBLE      NOT NULL
);

CREATE TABLE modification_otd
(
    modification_id INT REFERENCES modifications (id) ON DELETE CASCADE,
    otd_id          INT REFERENCES object_type_description (id) ON DELETE CASCADE
);

ALTER TABLE objects
    ADD COLUMN modification_id INT REFERENCES modifications (id) ON DELETE SET NULL;

