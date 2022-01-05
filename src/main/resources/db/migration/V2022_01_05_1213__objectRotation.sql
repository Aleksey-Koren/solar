ALTER TABLE objects
    ADD clockwise_rotation boolean null;

UPDATE objects
SET objects.clockwise_rotation = false
WHERE angle IS NOT NULL;

UPDATE objects JOIN object_type_description otd
    on objects.hull_id = otd.id
SET x              = rand(500),
    y              = rand(600),
    aphelion       = 0,
    orbital_period = 1,
    angle          = 0,
    speed_x        = 0,
    speed_y        = 0
WHERE otd.type = 'STATION';