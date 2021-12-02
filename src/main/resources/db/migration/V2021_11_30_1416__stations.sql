alter table object_type_description modify sub_type enum('STATIC', 'MINING', 'MILITARY', 'SCIENCE', 'PRODUCTION', 'ASYLUM') null;

CREATE TABLE stations
(
    id INT(11) PRIMARY KEY AUTO_INCREMENT
);

INSERT INTO permissions (title)
VALUES ('EDIT_STATION');

INSERT INTO stations (id)
SELECT ob.id FROM objects ob
    JOIN object_type_description otd on otd.id = ob.hull_id
WHERE otd.type = 'station';