package io.solar.mapper.objects;

import io.solar.dto.BasicObjectViewDto;
import io.solar.entity.objects.BasicObject;
import io.solar.service.ServiceException;
import org.springframework.stereotype.Service;

@Service
public class BasicObjectMapper {

    public BasicObjectViewDto toBasicObjectViewDto(BasicObject object) {
        BasicObjectViewDto dto = new BasicObjectViewDto();

        dto.setId(object.getId());
        dto.setPlanet(object.getPlanet() != null ? object.getPlanet().getId() : null);
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
        dto.setHullId(object.getObjectTypeDescription().getId());
        dto.setUserId(object.getUserId());
        dto.setActive(object.getActive());
        dto.setDurability(object.getDurability());
        dto.setAttachedToShip(object.getAttachedToShip() != null ? object.getAttachedToShip().getId() : null);
        dto.setAttachedToSocket(object.getAttachedToSocket());
        dto.setStatus(object.getStatus());
        dto.setAcceleration(object.getAcceleration());
        dto.setSpeed(object.getSpeed());

        return dto;
    }
}