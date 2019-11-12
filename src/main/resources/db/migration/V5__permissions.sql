create table permission_type (
    id int not null AUTO_INCREMENT,
    title varchar(255),
    primary key (id)
) CHARACTER SET utf8;

create table permission (
    id int not null AUTO_INCREMENT,
    permission_type int,
    user_id int,
    primary key (id),
    foreign key (permission_type) references permission_type(id),
    foreign key (user_id) references users(id)
) CHARACTER SET utf8;
