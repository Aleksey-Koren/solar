alter table goods
    modify owner int not null after amount;

alter table goods
    add buy_price bigint default 0 null after owner;

alter table goods
    change price sell_price bigint default 0 null;

alter table goods
    modify id bigint auto_increment first;

alter table goods
    add available_for_sale boolean not null;

alter table goods
    add available_for_buy boolean not null;