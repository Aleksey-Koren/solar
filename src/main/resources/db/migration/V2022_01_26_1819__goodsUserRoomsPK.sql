ALTER TABLE goods
    DROP CONSTRAINT goods_ibfk_1,
    DROP CONSTRAINT goods_ibfk_2,
    DROP PRIMARY KEY,
ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY;

ALTER TABLE goods
    ADD CONSTRAINT goods_ibfk_1 FOREIGN KEY (owner) REFERENCES objects (id) ON DELETE CASCADE,
    ADD CONSTRAINT goods_ibfk_2 FOREIGN KEY (product) REFERENCES products (id) ON DELETE CASCADE,
    ADD CONSTRAINT owner_product_unique UNIQUE (owner, product);

ALTER TABLE users_rooms
    DROP CONSTRAINT users_rooms_rooms_id_fk,
    DROP CONSTRAINT users_rooms_users_id_fk,
    DROP PRIMARY KEY,
    ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY;

ALTER TABLE users_rooms
    ADD CONSTRAINT users_rooms_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    ADD CONSTRAINT users_rooms_rooms FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE,
    ADD CONSTRAINT user_id_room_id UNIQUE (user_id, room_id);
