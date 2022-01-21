ALTER TABLE messages
    MODIFY message VARCHAR(2500);

    CREATE TABLE station_shops
    (
        id INT PRIMARY KEY AUTO_INCREMENT,
        station INT REFERENCES stations (id),
        shop_level INT NOT NULL
    );

    CREATE TABLE station_shops_otds
    (
        shop_id INT REFERENCES station_shops (id),
        otd_id INT REFERENCES object_type_description (id)
    );
