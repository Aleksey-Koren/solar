create table user_ship (
    id int not null AUTO_INCREMENT,
    user_id int not null,
    hull_id int not null,
    title varchar(255),
    durability int,
    mass int,
    data varchar(255),
    primary key (id),
    foreign key (user_id) references users(id),
    foreign key (hull_id) references inventory_item(id)
) CHARACTER SET utf8;

alter table users add column active_ship int;
alter table users add foreign key (active_ship) references user_ship(id);
alter table users add column money int default 0;

ALTER TABLE `user_ship` DROP FOREIGN KEY `user_ship_ibfk_1`;
ALTER TABLE `users` DROP FOREIGN KEY `users_ibfk_1`;
drop table user_ship;

alter table users drop column active_ship;
alter table users add column active_ship int;
alter table users add foreign key (active_ship) references stations(id);


alter table stations add column hull_id int;
alter table stations add foreign key (hull_id) references inventory_item(id);

RENAME TABLE stations TO user_ship;

alter table user_ship add column type1 enum('static', 'mining', 'military', 'science', 'production', 'asylum', 'ship') not null;
update user_ship set type1 = type;
alter table user_ship drop column type;

alter table user_ship change column type1 type enum('static', 'mining', 'military', 'science', 'production', 'asylum', 'ship');

alter table user_ship add column user_id int;
alter table user_ship add foreign key (user_id) references users(id);