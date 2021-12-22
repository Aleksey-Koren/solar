create table messages
(
    id           int auto_increment,
    sender_id    int          	 not null,
    recipient_id int          	 not null,
    message      varchar(255) 	 not null,
    created_at   timestamp       not null,
    constraint messages_pk
        primary key (id),
    constraint messages_users_id_fk
        foreign key (sender_id) references users (id),
    constraint messages_users_recepient_fk
        foreign key (recipient_id) references users (id)
);

create unique index messages_id_uindex
    on messages (id);

create table rooms
(
    id         int auto_increment,
    title      varchar(100)    not null,
    created_at timestamp       not null,
    owner_id   int             not null,
    constraint rooms_pk
        primary key (id),
    constraint rooms_users_id_fk
        foreign key (owner_id) references users (id)
);

create unique index rooms_id_uindex
    on rooms (id);

create table users_rooms
(
    user_id       int       not null,
    room_id       int       not null,
    subscribed_at timestamp null,
    constraint users_rooms_pk
        primary key (user_id, room_id),
    constraint users_rooms_rooms_id_fk
        foreign key (room_id) references rooms (id),
    constraint users_rooms_users_id_fk
        foreign key (user_id) references users (id)
);

alter table messages
    add room_id int not null after recipient_id;

alter table messages
    add constraint messages_rooms_id_fk
        foreign key (room_id) references rooms (id);

create table message_views
(
    message_id int       not null,
    user_id    int       not null,
    viewed_at  timestamp null,
    constraint message_views_pk
        primary key (message_id, user_id),
    constraint message_views_messages_id_fk
        foreign key (message_id) references messages (id),
    constraint message_views_users_id_fk
        foreign key (user_id) references users (id)
);