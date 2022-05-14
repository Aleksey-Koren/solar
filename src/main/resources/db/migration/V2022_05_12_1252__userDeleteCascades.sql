alter table users_permissions
drop foreign key users_permissions_ibfk_1;

alter table users_permissions
    add constraint users_permissions_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade;


alter table exchange_offers
drop foreign key exchange_offers_ibfk_2;

alter table exchange_offers
    add constraint exchange_offers_ibfk_2
        foreign key (user_id) references users (id)
            on delete cascade;


alter table exchanges
drop foreign key exchanges_ibfk_1;

alter table exchanges
    add constraint exchanges_ibfk_1
        foreign key (first_user_id) references users (id)
            on delete cascade;

alter table exchanges
drop foreign key exchanges_ibfk_2;

alter table exchanges
    add constraint exchanges_ibfk_2
        foreign key (second_user_id) references users (id)
            on delete cascade;


alter table marketplace_lots
drop foreign key marketplace_lots_ibfk_2;

alter table marketplace_lots
    add constraint marketplace_lots_ibfk_2
        foreign key (owner_id) references users (id)
            on delete cascade;

alter table star_ships
drop foreign key star_ships_users_id_fk;

alter table star_ships
    add constraint star_ships_users_id_fk
        foreign key (user_id) references users (id)
            on delete cascade;

