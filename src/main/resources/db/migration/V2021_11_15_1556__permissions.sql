RENAME TABLE IF EXISTS `user` TO users;

DROP TABLE IF EXISTS permission;
RENAME TABLE IF EXISTS permission_type TO permissions;

DROP TABLE IF EXISTS users_permissions;
CREATE TABLE users_permissions (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) DEFAULT NULL,
    `permission_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `user_id` (`user_id`),
    KEY `permission_id` (`permission_id`),
    CONSTRAINT `users_permissions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `users_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
