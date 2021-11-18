UPDATE  solar.permissions
SET title = 'PLAY_THE_GAME' where id = 1;

UPDATE  solar.permissions
SET title = 'EDIT_PLANET' where id = 2;

INSERT INTO solar.permissions  (title)
VALUES  ('EDIT_USER'),
        ('EDIT_PERMISSION'),
        ('ASSIGN_PERMISSION'),
        ('REVOKE_PERMISSION');

DROP TABLE IF EXISTS solar.user_permission_type;