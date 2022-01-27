package io.solar.mapper;

import io.solar.dto.GoodsDto;
import io.solar.dto.object.StarShipDto;
import io.solar.entity.Goods;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.StarShip;
import io.solar.service.GoodsService;
import io.solar.service.PlanetService;
import io.solar.service.StarShipService;
import io.solar.service.UserService;
import io.solar.service.object.BasicObjectService;
import io.solar.service.object.ObjectTypeDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StarShipMapper implements EntityDtoMapper<StarShip, StarShipDto> {

    private final StarShipService starShipService;
    private final PlanetService planetService;
    private final ObjectTypeDescriptionService objectTypeDescriptionService;
    private final BasicObjectService basicObjectService;
    private final UserService userService;
    private final GoodsMapper goodsMapper;

    @Override
    public StarShip toEntity(StarShipDto dto) {


        return dto.getId() == null
                ? fillStarshipFields(new StarShip(), dto)
                : fillStarshipFields(starShipService.getById(dto.getId()), dto);
    }

    @Override
    public StarShipDto toDto(StarShip entity) {
        List<GoodsDto> goodsDto = entity.getGoods()
                .stream()
                .map(goodsMapper::toDto)
                .toList();

        return StarShipDto.builder()
                .id(entity.getId())
                .planet(entity.getPlanet() != null ? entity.getPlanet().getId() : null)
                .population(entity.getPopulation())
                .fraction(entity.getFraction())
                .title(entity.getTitle())
                .x(entity.getX())
                .y(entity.getY())
                .aphelion(entity.getAphelion())
                .orbitalPeriod(entity.getOrbitalPeriod())
                .angle(entity.getAngle())
                .rotationAngle(entity.getRotationAngle())
                .hullId(entity.getObjectTypeDescription().getId())
                .userId(entity.getUser().getId())
                .active(entity.getActive())
                .durability(entity.getDurability())
                .attachedToShip(entity.getAttachedToShip() != null ? entity.getAttachedToShip().getId() : null)
                .attachedToSocket(entity.getAttachedToSocket())
                .status(entity.getStatus())
                .speedX(entity.getSpeedX())
                .speedY(entity.getSpeedY())
                .accelerationX(entity.getAccelerationX())
                .accelerationY(entity.getAccelerationY())
                .positionIterationTs(entity.getPositionIterationTs())
                .clockwiseRotation(entity.getClockwiseRotation())
                .volume(entity.getVolume())
                .goods(goodsDto)
                .build();
    }


    private StarShip fillStarshipFields(StarShip starShip, StarShipDto dto) {
        List<Goods> goods = dto.getGoods() != null
                ? dto.getGoods().stream().map(goodsMapper::toEntity).toList()
                : null;

        starShip.setPlanet(dto.getPlanet() != null ? planetService.findById(dto.getPlanet()) : null);
        starShip.setPopulation(dto.getPopulation());
        starShip.setFraction(dto.getFraction());
        starShip.setTitle(dto.getTitle());
        starShip.setX(dto.getX());
        starShip.setY(dto.getY());
        starShip.setAphelion(dto.getAphelion());
        starShip.setOrbitalPeriod(dto.getOrbitalPeriod());
        starShip.setAngle(dto.getAngle());
        starShip.setRotationAngle(dto.getRotationAngle());
        starShip.setObjectTypeDescription(dto.getHullId() != null ? objectTypeDescriptionService.getById(dto.getHullId()) : null);
        starShip.setUser(userService.getById(dto.getUserId()));
        starShip.setActive(dto.getActive());
        starShip.setDurability(dto.getDurability());
        starShip.setAttachedToShip(dto.getAttachedToShip() != null ? basicObjectService.getById(dto.getAttachedToShip()) : null);
        starShip.setAttachedToSocket(dto.getAttachedToSocket());
        starShip.setStatus(dto.getStatus());
        starShip.setSpeedX(dto.getSpeedX());
        starShip.setSpeedY(dto.getSpeedY());
        starShip.setAccelerationX(dto.getAccelerationX());
        starShip.setAccelerationY(dto.getAccelerationY());
        starShip.setPositionIterationTs(dto.getPositionIterationTs());
        starShip.setClockwiseRotation(dto.getClockwiseRotation());
        starShip.setVolume(dto.getVolume());
        starShip.setGoods(goods);

        return starShip;
    }
}
