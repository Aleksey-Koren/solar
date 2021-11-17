package io.solar.mapper;

public interface EntityDtoMapper<EntityType, DtoType> {
    EntityType toEntity(DtoType dto);
    DtoType toDto(EntityType entity);
}
