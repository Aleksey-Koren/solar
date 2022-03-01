alter table objects
    add constraint objects_object_type_socket_id_fk
        foreign key (attached_to_socket) references object_type_socket (id);