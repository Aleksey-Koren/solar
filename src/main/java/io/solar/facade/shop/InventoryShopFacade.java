package io.solar.facade.shop;

import io.solar.dto.shop.ShopDto;
import io.solar.dto.shop.StationShopDto;
import io.solar.entity.User;
import io.solar.entity.modification.Modification;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import io.solar.entity.shop.StationShop;
import io.solar.mapper.shop.StationShopMapper;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import io.solar.service.engine.interfaces.ObjectEngine;
import io.solar.service.modification.ModificationPriceService;
import io.solar.service.object.BasicObjectService;
import io.solar.service.object.ObjectTypeDescriptionService;
import io.solar.service.shop.StationShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryShopFacade {

    private final StationShopService stationShopService;
    private final UserService userService;
    private final ObjectTypeDescriptionService otdService;
    private final BasicObjectService basicObjectService;
    private final StarShipService starShipService;
    private final ObjectEngine objectEngine;
    private final InventoryEngine inventoryEngine;
    private final StationShopMapper stationShopMapper;
    private final StationService stationService;
    private final ModificationPriceService modificationPriceService;

    @Value("${station_sell_modifier:0.7}")
    private Double stationSellModifier;

    public StationShopDto getShopByStationId(Long stationId) {
        StationShop shop = stationShopService.findShopByStationId(stationId);

        return stationShopMapper.toDto(shop);
    }

    public HttpStatus buyInventory(User user, List<ShopDto> shopDto) {

        long amount = calculateAmountPrice(shopDto);
        userService.decreaseUserBalance(user, amount);

        List<BasicObject> objectsToBy = createObjects(shopDto);
        inventoryEngine.putToInventory(starShipService.getById(user.getLocation().getId()), objectsToBy);
        return HttpStatus.OK;
    }

    public HttpStatus sellInventory(User user, ShopDto shopDto) {

        List<BasicObject> objects = basicObjectService.findExactlyAllById(shopDto.getObjectIds());

        long sellAmount = objects.stream()
                .peek(obj -> {
                    if (!inventoryEngine.isInSpaceTechInventory(user.getLocation(), obj)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                String.format("Fail to sell object id = %d. Reason: object isn't in ship inventory", obj.getId()));
                    }
                }).map(this::calculateSellPrice).mapToLong(Long::longValue).sum();

        userService.increaseUserBalance(user, sellAmount);
        basicObjectService.deleteAll(objects);
        return HttpStatus.OK;
    }

    public Long getSellPrice(User user, Long objectId) {
        BasicObject object = basicObjectService.getById(objectId);

        if (!inventoryEngine.isInSpaceTechInventory(user.getLocation(), object)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Fail to get sell price for object id = %d. Reason: object isn't in ship inventory", object.getId()));
        }

        return calculateSellPrice(object);
    }

    public long calculateSellPrice(BasicObject item) {
        double durabilityModifier = (double) item.getDurability() / item.getObjectTypeDescription().getDurability();
        return (long) ((item.getObjectTypeDescription().getPrice().longValue() + calculateModifications(item))
                * stationSellModifier * durabilityModifier);
    }

    private long calculateModifications(BasicObject object) {
        Modification current = object.getModification();
        Station station = stationService.getById(object.getAttachedToShip().getAttachedToShip().getId());

        return object.getModification().getModificationType().getModifications().stream()
                .filter(modification -> modification.getLevel() <= current.getLevel())
                .mapToLong(modification -> modificationPriceService.findByStationAndModification(station, modification)
                        .map(modificationPrice -> modificationPrice.getPrice().getMoneyAmount()).orElse(0L))
                .sum();
    }


    private long calculateAmountPrice(List<ShopDto> shopDto) {
        return shopDto.stream()
                .map(dto -> ((long) otdService.getById(dto.getOtdId()).getPrice() * dto.getQuantity()))
                .mapToLong(Long::longValue).sum();
    }

    private List<BasicObject> createObjects(List<ShopDto> shopDto) {
        return shopDto.stream()
                .map(dto -> objectEngine.createInventoryObjects(otdService.getById(dto.getOtdId()), dto.getQuantity()))
                .flatMap(List::stream)
                .toList();
    }
}