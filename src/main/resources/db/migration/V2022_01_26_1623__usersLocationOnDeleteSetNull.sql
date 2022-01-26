alter table users drop foreign key users_objects_id_fk;

alter table users
    add constraint users_objects_id_fk
        foreign key (location) references objects (id)
            on delete set null;