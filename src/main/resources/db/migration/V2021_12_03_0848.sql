INSERT INTO solar.permissions (title) value ('EDIT_INVENTORY');

alter table object_type_description modify type enum('STATION', 'SHIP', 'ITEM', 'ASTEROID') null;