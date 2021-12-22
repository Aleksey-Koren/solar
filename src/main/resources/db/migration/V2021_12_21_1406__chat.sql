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

create table messages
(
    id         int auto_increment,
    sender_id  int          not null,
    room_id    int          not null,
    message    varchar(255) not null,
    created_at timestamp    not null,
    constraint messages_pk
        primary key (id),
    constraint messages_rooms_id_fk
        foreign key (room_id) references rooms (id),
    constraint messages_users_id_fk
        foreign key (sender_id) references users (id)
);

create unique index messages_id_uindex
    on messages (id);

create unique index rooms_id_uindex
    on rooms (id);

create table users_rooms
(
    user_id       int       not null,
    room_id       int       not null,
    subscribed_at timestamp null,
    last_seen_at  timestamp null,
    constraint users_rooms_pk
        primary key (user_id, room_id),
    constraint users_rooms_rooms_id_fk
        foreign key (room_id) references rooms (id),
    constraint users_rooms_users_id_fk
        foreign key (user_id) references users (id)
);