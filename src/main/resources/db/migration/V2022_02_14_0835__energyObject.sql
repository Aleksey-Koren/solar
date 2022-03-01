alter table objects
    add energy_consumption bigint null;

update objects JOIN object_type_description otd on otd.id = objects.hull_id
SET objects.energy_consumption = otd.energy_consumption WHERE objects.energy_consumption is null;

alter table objects
    add is_enabled boolean null;