package io.solar.facade.shop;

import io.solar.dto.shop.ShopDto;
import io.solar.dto.shop.StationShopDto;
import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.shop.StationShop;
import io.solar.mapper.shop.StationShopMapper;
import io.solar.repository.ObjectTypeDescriptionRepository;
import io.solar.repository.UserRepository;
import io.solar.service.engine.interfaces.InventoryEngine;
import io.solar.service.engine.interfaces.ObjectEngine;
import io.solar.service.shop.StationShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StationShopFacade {

    private final StationShopService stationShopService;
    private final StationShopMapper stationShopMapper;
    private final InventoryEngine inventoryEngine;
    private final ObjectEngine objectEngine;
    private final ObjectTypeDescriptionRepository OTDRepository;
    private final UserRepository userRepository;


    public StationShopDto getShopByStationId(Long stationId) {
        StationShop shop = stationShopService.findShopByStationId(stationId);

        return stationShopMapper.toDto(shop);
    }

    public HttpStatus buyInventory(User user, List<ShopDto> shopDto) {

        long amount = calculateAmountPrice(shopDto);
        if (amount > user.getMoney()) {
            return HttpStatus.BAD_REQUEST;
        }else{
            user.setMoney(user.getMoney() - amount);
            userRepository.save(user);
        }

        List<BasicObject> objectsToBy = createObjects(shopDto);
        inventoryEngine.putToInventory(user.getLocation(), objectsToBy);
        return HttpStatus.OK;
    }

    private long calculateAmountPrice(List<ShopDto> shopDto) {
        return shopDto.stream()
                .map(dto -> ((long) OTDRepository.getById(dto.getOtdId()).getPrice() * dto.getQuantity()))
                .mapToLong(Long::longValue).sum();
    }

    private List<BasicObject> createObjects(List<ShopDto> shopDto) {
        return shopDto.stream()
                .map(dto -> {
                    List<BasicObject> objects = new ArrayList<>();
                    for (int i = 0; i < dto.getQuantity(); i++) {
                        objects.add(objectEngine.createInventoryObject(OTDRepository.getById(dto.getOtdId())));
                    }
                    return objects;
                })
                .flatMap(List::stream)
                .toList();
    }
}
