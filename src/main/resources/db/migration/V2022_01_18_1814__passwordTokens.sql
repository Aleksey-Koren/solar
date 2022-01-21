create table password_tokens
(
    user_id      int          not null,
    token        varchar(128) not null,
    expire_at    timestamp    not null,
    is_activated boolean      not null,
    constraint password_tokens_pk
        primary key (user_id),
    constraint password_tokens_users_id_fk
        foreign key (user_id) references users (id)
            on delete cascade
);

