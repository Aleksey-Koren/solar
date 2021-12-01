package io.solar.mapper.objects;

import io.solar.dto.ItemObjectDto;
import io.solar.entity.objects.BasicObject;
import io.solar.service.ServiceException;
import org.springframework.stereotype.Service;

@Service
public class BasicObjectMapper {

    public ItemObjectDto toItemObjectDto(BasicObject object) {
        ItemObjectDto dto = new ItemObjectDto();

        dto.setId(object.getId());
        dto.setPlanetId(object.getPlanet() != null ? object.getPlanet().getId() : null);
        dto.setFraction(object.getFraction());
        dto.setTitle(object.getTitle());
        dto.setX(object.getX());
        dto.setY(object.getY());
        dto.setAphelion(object.getAphelion());
        dto.setOrbitalPeriod(object.getOrbitalPeriod());
        dto.setAngle(object.getAngle());
        if (object.getObjectTypeDescription() == null) {
            throw new ServiceException("Object must not exist without an ObjectTypeDescription field");
        }
        dto.setObjectTypeDescriptionId(object.getObjectTypeDescription().getId());
        dto.setUserId(object.getUserId());
        dto.setActive(object.getActive());
        dto.setDurability(object.getDurability());
        dto.setAttachedToShip(object.getAttachedToShip() != null ? object.getAttachedToShip().getId() : null);
        dto.setAttachedToSocket(object.getAttachedToSocket());
        dto.setObjectStatus(object.getStatus());
        dto.setAcceleration(object.getAcceleration());
        dto.setSpeed(object.getSpeed());

        return dto;
    }
}