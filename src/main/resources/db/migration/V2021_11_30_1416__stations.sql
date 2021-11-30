alter table object_type_description modify sub_type enum('STATIC', 'MINING', 'MILITARY', 'SCIENCE', 'PRODUCTION', 'ASYLUM') null;

CREATE TABLE stations
(
    id INT(11) PRIMARY KEY AUTO_INCREMENT
);