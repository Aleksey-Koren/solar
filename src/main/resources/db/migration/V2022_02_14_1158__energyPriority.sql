CREATE TABLE space_tech_sockets
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    space_tech_id INT REFERENCES objects(id) ON DELETE CASCADE,
    socket_id INT REFERENCES object_type_socket(id) ON DELETE CASCADE,
    energy_consumption_priority INT
);