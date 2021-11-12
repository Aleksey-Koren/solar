DROP TABLE IF EXISTS user_permission_type;

CREATE TABLE IF NOT EXISTS user_permission_type
(
    `user_id`            int NOT NULL,
    `permission_type_id` int NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;
