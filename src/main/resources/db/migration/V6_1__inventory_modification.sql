create table inventory_modification (
    id int not null AUTO_INCREMENT,
    title varchar(255),
    data varchar(255),
    primary key (id)
) CHARACTER SET utf8;

create table inventory_item_modification (
    id int not null AUTO_INCREMENT,
    item_id int not null,
    modification_id int not null,
    primary key (id),
    foreign key (item_id) references inventory_item(id),
    foreign key (modification_id) references inventory_modification(id)
) CHARACTER SET utf8;

create table inventory_item_socket (
    id int not null AUTO_INCREMENT,
    item_id int not null,
    item_type_id int not null,
    primary key (id),
    foreign key (item_id) references inventory_item(id),
    foreign key (item_type_id) references inventory_type(id)
) CHARACTER SET utf8;
