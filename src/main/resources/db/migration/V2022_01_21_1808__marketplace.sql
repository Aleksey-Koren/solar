create table marketplace_lots
(
    id                  bigint    not null,
    object_id           int       not null,
    owner_id            int       not null,
    start_date          timestamp not null,
    finish_date         timestamp not null,
    start_price         bigint    not null,
    instant_price       bigint    not null,
    is_buyer_has_taken  boolean   not null,
    is_seller_has_taken boolean   not null,
    constraint marketplace_lots_pk
        primary key (id),
    constraint marketplace_lots_objects_id_fk
        foreign key (object_id) references objects (id),
    constraint marketplace_lots_users_id_fk
        foreign key (owner_id) references users (id)
);

create table lots_bets
(
    lot_id   bigint    not null,
    user_id  int       not null,
    amount   bigint    null,
    bet_time timestamp not null,
    constraint lots_bets_pk
        primary key (lot_id),
    constraint lots_bets_marketplace_lots_id_fk
        foreign key (lot_id) references marketplace_lots (id)
);

alter table lots_bets
    add constraint lots_bets_users_id_fk
        foreign key (user_id) references users (id);

