SET FOREIGN_KEY_CHECKS=0;
ALTER TABLE `solar`.`users` DROP FOREIGN KEY `users_ibfk_1`;
SET FOREIGN_KEY_CHECKS=1;
ALTER TABLE `solar`.`users`
    DROP COLUMN `active_ship`,
    DROP INDEX `active_ship`;
alter table user_ship add column active int(1);