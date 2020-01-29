alter table objects add column attached_to_ship int(11) references objects(id);
alter table objects add column attached_to_socket int(11) references object_type_socket(id);
alter table objects drop column in_space;
alter table objects add column status enum ('in_space', 'attached_to', 'in_container');
alter table object_type_socket add column alias varchar(255);