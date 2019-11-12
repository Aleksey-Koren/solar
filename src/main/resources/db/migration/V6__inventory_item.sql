create table inventory_item (
    id int not null AUTO_INCREMENT,
    inventory_type int,
    title varchar(255),
    power_min float,
    power_max float,
    power_degradation float,
    cooldown float,
    distance float,
    energy_consumption int,
    durability int,
    description text,
    mass int,
    price int,
    primary key (id),
    foreign key (inventory_type) references inventory_type(id)
) CHARACTER SET utf8;
