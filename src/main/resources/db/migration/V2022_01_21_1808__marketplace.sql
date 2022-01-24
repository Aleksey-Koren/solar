
create table marketplace_lots
(
    id                  bigint auto_increment primary key,
    object_id           int       not null references objects (id),
    owner_id            int       not null references users (id),
    start_date          timestamp not null,
    finish_date         timestamp not null,
    start_price         bigint    not null,
    instant_price       bigint    not null,
    is_buyer_has_taken  boolean   not null,
    is_seller_has_taken boolean   not null
);

create table marketplace_bets
(
    id bigint auto_increment primary key,
    lot_id bigint references marketplace_lots (id) on delete cascade,
    user_id  int not null references users (id) on delete cascade,
    amount   bigint    null,
    bet_time timestamp not null,
    unique (lot_id, amount)
);
