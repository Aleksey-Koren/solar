create table stations (
                          id int not null AUTO_INCREMENT,
                          planet int,
                          population int,
                          fraction varchar(255),
                          title varchar(255),
                          type varchar(255),
                          primary key (id),
                          foreign key (planet) references planets(id)
)CHARACTER SET utf8;


create table productions (
                             id int not null AUTO_INCREMENT,
                             power float,
                             product int,
                             station int,
                             primary key (id),
                             foreign key (product) references products(id),
                             foreign key (station) references stations(id)
)CHARACTER SET utf8;

alter table stations add column x float;
alter table stations add column y float;
alter table stations add column aphelion float;
alter table stations add column orbital_period float;
alter table stations add column angle float;