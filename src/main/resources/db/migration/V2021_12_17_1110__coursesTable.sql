CREATE TABLE courses
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    object_id INT REFERENCES objects(id),
    x FLOAT NOT NULL,
    y FLOAT NOT NULL,
    acceleration_x FLOAT NOT NULL,
    acceleration_y FLOAT NOT NULL,
    next BIGINT REFERENCES courses(id),
    created_at TIMESTAMP,
    expire_at TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;