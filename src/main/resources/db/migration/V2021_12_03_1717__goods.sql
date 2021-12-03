CREATE TABLE goods
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner INT REFERENCES objects (id),
    product INT REFERENCES products (id),
    amount INT NOT NULL
)