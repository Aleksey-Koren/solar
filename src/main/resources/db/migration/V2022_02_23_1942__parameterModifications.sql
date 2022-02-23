ALTER TABLE parameter_modifications
    ADD CONSTRAINT param_type_modification_unique UNIQUE (parameter_type, modification_id);