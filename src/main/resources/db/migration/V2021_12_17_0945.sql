ALTER TABLE objects
    ADD position_iteration_ts bigint null;

ALTER TABLE objects
    ADD position_iteration bigint null;

ALTER TABLE objects
    ALTER COLUMN position_iteration set default 1;

INSERT INTO utilities (`util_key`, `util_value`)
VALUES
    ('position_iteration', '1');
