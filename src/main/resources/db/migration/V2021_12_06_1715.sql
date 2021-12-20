alter table stations
    add constraint stations_objects__fk
        foreign key (id) references objects (id);

alter table star_ships
    add constraint star_ships_objects__fk
        foreign key (id) references objects (id);