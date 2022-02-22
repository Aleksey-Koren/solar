CREATE TABLE modification_otd
(
    modification_id INT REFERENCES modifications (id) ON DELETE CASCADE,
    otd_id          INT REFERENCES object_type_description (id) ON DELETE CASCADE
);

CREATE TABLE prices
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    money_amount BIGINT
);

CREATE TABLE price_products
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    price_id BIGINT REFERENCES prices (id) ON DELETE CASCADE NOT NULL,
    product_id INT REFERENCES products (id) ON DELETE CASCADE NOT NULL,
    product_amount INT NOT NULL,
    UNIQUE (price_id, product_id)
);

CREATE TABLE modification_types
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(300)
);

ALTER TABLE modifications
    ADD COLUMN modification_type_id INT REFERENCES modification_types (id),
    ADD COLUMN level TINYINT NULL;