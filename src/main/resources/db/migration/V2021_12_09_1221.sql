INSERT INTO solar.object_type_description (inventory_type, title, power_min, power_max, power_degradation, cooldown,
                                           distance, energy_consumption, durability, description, mass, price, type,
                                           sub_type)
VALUES (21, 'Planet', null, null, null, null, null, null, null, null, null, null, null, null);

UPDATE objects
SET hull_id = 84
WHERE hull_id is null;