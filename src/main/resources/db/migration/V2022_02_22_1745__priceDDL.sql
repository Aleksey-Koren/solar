CREATE TABLE prices
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    money_amount BIGINT
);

CREATE TABLE price_products
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    price_id BIGINT NOT NULL REFERENCES prices (id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products (id) ON DELETE CASCADE,
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
    ADD COLUMN level TINYINT NOT NULL;

CREATE TABLE modification_prices
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    modification_id INT NOT NULL REFERENCES modifications (id) ON DELETE CASCADE,
    price_id BIGINT NOT NULL REFERENCES prices (id) ON DELETE CASCADE,
    station_id INT NOT NULL REFERENCES stations (id) ON DELETE CASCADE
);