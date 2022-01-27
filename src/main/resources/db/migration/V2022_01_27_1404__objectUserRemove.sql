alter table objects
drop
foreign key objects_ibfk_3;

drop index user_id on objects;

alter table objects
drop
column user_id;

alter table star_ships
    add user_id int null;

alter table star_ships
    add constraint star_ships_users_id_fk
        foreign key (user_id) references users (id);

alter table star_ships
    modify user_id int not null;

alter table stations
    add user_id int null;

alter table stations
    add constraint stations_users_id_fk
        foreign key (user_id) references users (id);