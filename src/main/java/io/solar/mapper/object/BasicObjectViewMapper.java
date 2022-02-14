package io.solar.mapper.object;

import io.solar.dto.object.BasicObjectViewDto;
import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.mapper.EntityDtoMapper;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.ObjectTypeDescriptionRepository;
import io.solar.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BasicObjectViewMapper implements EntityDtoMapper<BasicObject, BasicObjectViewDto> {

    private final ObjectTypeDescriptionRepository objectTypeDescriptionRepository;
    private final BasicObjectRepository basicObjectRepository;
    private final PlanetRepository planetRepository;

    @Override
    public BasicObject toEntity(BasicObjectViewDto dto) {

        return dto.getId() == null
                ? createBasicObject(dto)
                : findBasicObject(dto);
    }

    @Override
    public BasicObjectViewDto toDto(BasicObject entity) {

        return BasicObjectViewDto.builder()
                .id(entity.getId())
                .population(entity.getPopulation())
                .planet(entity.getPlanet() != null ? entity.getPlanet().getId() : null)
                .fraction(entity.getFraction())
                .title(entity.getTitle())
                .x(entity.getX())
                .y(entity.getY())
                .aphelion(entity.getAphelion())
                .orbitalPeriod(entity.getOrbitalPeriod())
                .angle(entity.getAngle())
                .rotationAngle(entity.getRotationAngle())
                .hullId(entity.getObjectTypeDescription().getId())
                .active(entity.getActive())
                .durability(entity.getDurability())
                .attachedToShip(entity.getAttachedToShip() != null ? entity.getAttachedToShip().getId() : null)
                .attachedToSocket(entity.getAttachedToSocket())
                .status(entity.getStatus())
                .speedX(entity.getSpeedX())
                .speedY(entity.getSpeedY())
                .accelerationX(entity.getAccelerationX())
                .accelerationY(entity.getAccelerationY())
                .clockwiseRotation(entity.getClockwiseRotation())
                .volume(entity.getVolume())
                .energyConsumption(entity.getEnergyConsumption())
                .energyConsumptionPriority(entity.getEnergyConsumptionPriority())
                .enabled(entity.getEnabled())
                .build();
    }

    private BasicObject createBasicObject(BasicObjectViewDto dto) {
        BasicObject basicObject = new BasicObject();

        fillBasicObjectFields(basicObject, dto);

        return basicObject;
    }

    private BasicObject findBasicObject(BasicObjectViewDto dto) {

        BasicObject basicObject = basicObjectRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find basicObject with id = %d", dto.getId()))
                );

        fillBasicObjectFields(basicObject, dto);

        return basicObject;
    }

    private void fillBasicObjectFields(BasicObject basicObject, BasicObjectViewDto dto) {

        ObjectTypeDescription objectTypeDescription = objectTypeDescriptionRepository.findById(dto.getHullId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find objectTypeDescription with id = %d", dto.getHullId()))
                );

        BasicObject attachedToShip = basicObjectRepository.findById(dto.getAttachedToShip())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find attachedToShip with id = %d", dto.getAttachedToShip()))
                );

        Planet planet = planetRepository.findById(dto.getPlanet())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find planet with id = %d", dto.getPlanet()))
                );

        basicObject.setTitle(dto.getTitle());
        basicObject.setActive(dto.getActive());
        basicObject.setAngle(dto.getAngle());
        basicObject.setRotationAngle(dto.getRotationAngle());
        basicObject.setAphelion(dto.getAphelion());
        basicObject.setDurability(dto.getDurability());
        basicObject.setFraction(dto.getFraction());
        basicObject.setOrbitalPeriod(dto.getOrbitalPeriod());
        basicObject.setAccelerationX(dto.getAccelerationX());
        basicObject.setAccelerationY(dto.getAccelerationY());
        basicObject.setX(dto.getX());
        basicObject.setY(dto.getY());
        basicObject.setStatus(dto.getStatus());
        basicObject.setSpeedX(dto.getSpeedX());
        basicObject.setSpeedY(dto.getSpeedY());
        basicObject.setPlanet(planet);
        basicObject.setAttachedToSocket(dto.getAttachedToSocket());
        basicObject.setObjectTypeDescription(objectTypeDescription);
        basicObject.setAttachedToShip(attachedToShip);
        basicObject.setClockwiseRotation(dto.getClockwiseRotation() != null ? dto.getClockwiseRotation() : basicObject.getClockwiseRotation());
        basicObject.setVolume(dto.getVolume());
        basicObject.setEnergyConsumption(dto.getEnergyConsumption());
        basicObject.setEnergyConsumptionPriority(dto.getEnergyConsumptionPriority());
        basicObject.setEnabled(dto.getEnabled());
    }
}