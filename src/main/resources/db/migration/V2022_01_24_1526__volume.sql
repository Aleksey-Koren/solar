alter table object_type_description
    add volume float null;

alter table objects
    add volume float null;

alter table products
    add volume float not null;
