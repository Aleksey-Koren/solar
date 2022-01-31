CREATE TABLE exchanges
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_user_id INT NOT NULL REFERENCES users (id),
    second_user_id INT NOT NULL REFERENCES users (id),
    start_time TIMESTAMP DEFAULT now(),
    first_accepted BOOLEAN DEFAULT false,
    second_accepted BOOLEAN DEFAULT false
);

CREATE TABLE exchange_offers
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exchange_id BIGINT NOT NULL REFERENCES exchanges (id),
    user_id INT NOT NULL REFERENCES users (id),
    object_id INT REFERENCES objects (id),
    money_amount BIGINT,
    product_id INT REFERENCES products (id),
    product_amount BIGINT,
    created_at TIMESTAMP DEFAULT now(),
    offer_type VARCHAR(50) NOT NULL
);