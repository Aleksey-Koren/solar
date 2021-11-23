INSERT INTO permissions (title)
VALUES
    ('EDIT_PRODUCT'),
    ('EDIT_INVENTORY_TYPE');

ALTER TABLE product
RENAME TO products;