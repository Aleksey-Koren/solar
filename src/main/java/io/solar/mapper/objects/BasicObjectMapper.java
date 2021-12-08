package io.solar.mapper.objects;

import io.solar.dto.BasicObjectDto;
import io.solar.dto.BasicObjectViewDto;
import io.solar.dto.inventory.InventorySocketDto;
import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.mapper.EntityDtoMapper;
import io.solar.mapper.SocketMapper;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.ObjectTypeDescriptionRepository;
import io.solar.repository.PlanetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BasicObjectMapper implements EntityDtoMapper<BasicObject, BasicObjectDto> {

    private final BasicObjectRepository basicObjectRepository;
    private final ObjectTypeDescriptionRepository objectTypeDescriptionRepository;
    private final PlanetRepository planetRepository;
    private final BasicObjectViewMapper basicObjectViewMapper;
    private final SocketMapper socketMapper;


    @Autowired
    public BasicObjectMapper(BasicObjectRepository basicObjectRepository,
                             ObjectTypeDescriptionRepository objectTypeDescriptionRepository,
                             PlanetRepository planetRepository,
                             BasicObjectViewMapper basicObjectViewMapper,
                             SocketMapper socketMapper) {

        this.basicObjectRepository = basicObjectRepository;
        this.objectTypeDescriptionRepository = objectTypeDescriptionRepository;
        this.planetRepository = planetRepository;
        this.basicObjectViewMapper = basicObjectViewMapper;
        this.socketMapper = socketMapper;
    }

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

        List<InventorySocketDto> socketList = entity.getSocketList()
                .stream()
                .map(socketMapper::toDto)
                .collect(Collectors.toList());

        return BasicObjectDto.builder()
                .id(entity.getId())
                .acceleration(entity.getAcceleration())
                .active(entity.getActive())
                .aphelion(entity.getAphelion())
                .angle(entity.getAngle())
                .attachedToShip(entity.getAttachedToShip() == null ? null : entity.getAttachedToShip().getId())
                .attachedToSocket(entity.getAttachedToSocket())
                .durability(entity.getDurability())
                .fraction(entity.getFraction())
                .hullId(entity.getObjectTypeDescription().getId())
                .orbitalPeriod(entity.getOrbitalPeriod())
                .planet(entity.getPlanet() == null ? null : entity.getPlanet().getId())
                .x(entity.getX())
                .y(entity.getY())
                .speed(entity.getSpeed())
                .status(entity.getStatus())
                .population(entity.getPopulation())
                .title(entity.getTitle())
                .userId(entity.getUserId())
                .attachedObjects(attachedObjects)
                .socketList(socketList)
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


    private void fillBasicObjectFields(BasicObject basicObject, BasicObjectDto dto) {

        ObjectTypeDescription objectTypeDescription = objectTypeDescriptionRepository.findById(dto.getHullId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find object type description with id = %d", dto.getHullId())));

        Planet planet = planetRepository.findById(dto.getPlanet())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find planet with id = %d", dto.getPlanet())));

        BasicObject attachedToShip = basicObjectRepository.findById(dto.getAttachedToShip())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find attachedToShip with id = %d", dto.getAttachedToShip()))
                );

        basicObject.setPopulation(dto.getPopulation());
        basicObject.setAcceleration(dto.getAcceleration());
        basicObject.setActive(dto.getActive());
        basicObject.setAphelion(dto.getAphelion());
        basicObject.setAngle(dto.getAngle());
        basicObject.setAttachedToShip(attachedToShip);
        basicObject.setAttachedToSocket(dto.getAttachedToSocket());
        basicObject.setDurability(dto.getDurability());
        basicObject.setFraction(dto.getFraction());
        basicObject.setObjectTypeDescription(objectTypeDescription);
        basicObject.setOrbitalPeriod(dto.getOrbitalPeriod());
        basicObject.setPlanet(planet);
        basicObject.setX(dto.getX());
        basicObject.setY(dto.getY());
        basicObject.setSpeed(dto.getSpeed());
        basicObject.setStatus(dto.getStatus());
        basicObject.setPopulation(dto.getPopulation());
        basicObject.setTitle(dto.getTitle());
        basicObject.setUserId(dto.getUserId());
    }
}
