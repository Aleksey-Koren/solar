ALTER TABLE modifications
    ADD CONSTRAINT type_level_unique UNIQUE (modification_type_id, level);