alter table inventory_item rename to inventory_item_description;
alter table inventory_type rename to object_type;
alter table inventory_modification rename to object_modification_type;
alter table inventory_item_socket rename to object_type_socket;
alter table inventory_item_modification rename to object_modification;
alter table inventory_item_description rename to object_type_description;
alter table user_ship rename to objects;
alter table objects add column in_space int(1);
alter table objects drop column type;
alter table object_type_description add column type enum ('station', 'ship', 'item', 'asteroid');
alter table object_type_description add column sub_type enum ('static','mining','military','science','production','asylum');

alter table object_modification_type add column description varchar(255);
alter table object_type_socket add column sort_order int(11);

create table inventory_item (
    id int(11) not null,
    item_type int(11) not null
);