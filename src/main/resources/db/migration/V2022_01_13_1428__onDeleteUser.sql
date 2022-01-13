alter table messages
    modify sender_id int null;

alter table messages
drop
foreign key messages_users_id_fk;

alter table messages
    add constraint messages_users_id_fk
        foreign key (sender_id) references users (id)
            on delete set null;

alter table rooms
    modify owner_id int null;

alter table rooms
drop
foreign key rooms_users_id_fk;

alter table rooms
    add constraint rooms_users_id_fk
        foreign key (owner_id) references users (id)
            on delete set null;

alter table objects
drop
foreign key objects_ibfk_3;

alter table objects
    add constraint objects_ibfk_3
        foreign key (user_id) references users (id)
            on delete cascade;

alter table users_rooms
drop
foreign key users_rooms_rooms_id_fk;

alter table users_rooms
    add constraint users_rooms_rooms_id_fk
        foreign key (room_id) references rooms (id);

alter table users_rooms
drop
foreign key users_rooms_users_id_fk;

alter table users_rooms
    add constraint users_rooms_users_id_fk
        foreign key (user_id) references users (id);

alter table messages
    add edited_at timestamp null;