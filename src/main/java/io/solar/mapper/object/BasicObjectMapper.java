package io.solar.mapper.object;

import io.solar.dto.object.BasicObjectDto;
import io.solar.dto.object.BasicObjectViewDto;
import io.solar.dto.inventory.InventorySocketDto;
import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.mapper.EntityDtoMapper;
import io.solar.mapper.SocketMapper;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.ObjectTypeDescriptionRepository;
import io.solar.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicObjectMapper implements EntityDtoMapper<BasicObject, BasicObjectDto> {

    private final BasicObjectRepository basicObjectRepository;
    private final ObjectTypeDescriptionRepository objectTypeDescriptionRepository;
    private final PlanetRepository planetRepository;
    private final BasicObjectViewMapper basicObjectViewMapper;
    private final SocketMapper socketMapper;

    @Override
    public BasicObject toEntity(BasicObjectDto dto) {

        return dto.getId() == null
                ? createBasicObject(dto)
                : findBasicObject(dto);
    }

    @Override
    public BasicObjectDto toDto(BasicObject entity) {

        List<BasicObjectViewDto> attachedObjects = entity.getAttachedObjects()
                .stream()
                .map(basicObjectViewMapper::toDto)
                .collect(Collectors.toList());

        List<InventorySocketDto> socketList = entity.getObjectTypeDescription()
                .getSocketList()
                .stream()
                .map(socketMapper::toDto)
                .collect(Collectors.toList());

        return BasicObjectDto.builder()
                .id(entity.getId())
                .accelerationX(entity.getAccelerationX())
                .accelerationY(entity.getAccelerationY())
                .active(entity.getActive())
                .aphelion(entity.getAphelion())
                .angle(entity.getAngle())
                .rotationAngle(entity.getRotationAngle())
                .attachedToShip(entity.getAttachedToShip() == null ? null : entity.getAttachedToShip().getId())
                .attachedToSocket(entity.getAttachedToSocket())
                .durability(entity.getDurability())
                .fraction(entity.getFraction())
                .hullId(entity.getObjectTypeDescription().getId())
                .orbitalPeriod(entity.getOrbitalPeriod())
                .planet(entity.getPlanet() == null ? null : entity.getPlanet().getId())
                .x(entity.getX())
                .y(entity.getY())
                .speedX(entity.getSpeedX())
                .speedY(entity.getSpeedY())
                .status(entity.getStatus())
                .population(entity.getPopulation())
                .title(entity.getTitle())
                .attachedObjects(attachedObjects)
                .socketList(socketList)
                .positionIterationTs(entity.getPositionIterationTs())
                .clockwiseRotation(entity.getClockwiseRotation())
                .volume(entity.getVolume())
                .build();
    }

    private BasicObject findBasicObject(BasicObjectDto dto) {

        BasicObject basicObject = basicObjectRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find basic object with id = %d", dto.getId())));

        fillBasicObjectFields(basicObject, dto);

        return basicObject;
    }

    private BasicObject createBasicObject(BasicObjectDto dto) {
        BasicObject basicObject = new BasicObject();

        fillBasicObjectFields(basicObject, dto);

        return basicObject;
    }


    private void fillBasicObjectFields(BasicObject entity, BasicObjectDto dto) {

        ObjectTypeDescription objectTypeDescription = objectTypeDescriptionRepository.findById(dto.getHullId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find object type description with id = %d", dto.getHullId())));

        Planet planet = planetRepository.findById(dto.getPlanet())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find planet with id = %d", dto.getPlanet())));

        BasicObject attachedToShip = null;

        if (dto.getAttachedToShip() != null) {
            attachedToShip = basicObjectRepository.findById(dto.getAttachedToShip())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            String.format("Cannot find attachedToShip with id = %d", dto.getAttachedToShip()))
                    );
        }

        entity.setPopulation(dto.getPopulation());
        entity.setAccelerationX(dto.getAccelerationX());
        entity.setAccelerationY(dto.getAccelerationY());
        entity.setActive(dto.getActive());
        entity.setAphelion(dto.getAphelion());
        entity.setAngle(dto.getAngle());
        entity.setRotationAngle(dto.getRotationAngle());
        entity.setAttachedToShip(attachedToShip);
        entity.setAttachedToSocket(dto.getAttachedToSocket());
        entity.setDurability(dto.getDurability());
        entity.setFraction(dto.getFraction());
        entity.setObjectTypeDescription(objectTypeDescription);
        entity.setOrbitalPeriod(dto.getOrbitalPeriod());
        entity.setPlanet(planet);
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        entity.setSpeedX(dto.getSpeedX());
        entity.setSpeedY(dto.getSpeedY());
        entity.setStatus(dto.getStatus());
        entity.setPopulation(dto.getPopulation());
        entity.setTitle(dto.getTitle());
        entity.setPositionIterationTs(dto.getPositionIterationTs());
        entity.setClockwiseRotation(dto.getClockwiseRotation() != null ? dto.getClockwiseRotation() : entity.getClockwiseRotation());
        entity.setVolume(dto.getVolume());
    }
}