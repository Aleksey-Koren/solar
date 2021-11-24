create table utilities

(
    `util_key` varchar(128) not null primary key,
    `util_value` varchar(128) not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO utilities (`util_key`, `util_value`)
VALUES
    ('admin_not_exists', 'yes');