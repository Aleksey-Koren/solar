alter table courses
drop foreign key courses_ibfk_2;

alter table courses
    add constraint courses_ibfk_2
        foreign key (next) references courses (id)
            on delete set null;

alter table courses
drop foreign key courses_ibfk_3;

alter table courses
    add constraint courses_ibfk_3
        foreign key (previous) references courses (id)
            on delete set null;

