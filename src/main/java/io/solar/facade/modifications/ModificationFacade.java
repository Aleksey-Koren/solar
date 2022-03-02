package io.solar.facade.modifications;

import io.solar.dto.modification.ApplyModificationDto;
import io.solar.dto.modification.ModificationDto;
import io.solar.dto.transfer.TransferProductsDto;
import io.solar.entity.User;
import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ModificationPrice;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.mapper.modification.ModificationMapper;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.MoneyEngine;
import io.solar.service.engine.interfaces.ProductEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import io.solar.service.engine.interfaces.modification.ModificationEngine;
import io.solar.service.engine.interfaces.modification.ModificationPriceEngine;
import io.solar.service.modification.ModificationPriceService;
import io.solar.service.modification.ModificationService;
import io.solar.service.object.BasicObjectService;
import io.solar.specification.ModificationSpecification;
import io.solar.specification.filter.ModificationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModificationFacade {

    private final ModificationMapper modificationMapper;
    private final ModificationService modificationService;
    private final UserService userService;
    private final BasicObjectService basicObjectService;
    private final InventoryEngine inventoryEngine;
    private final SpaceTechEngine spaceTechEngine;
    private final StationService stationService;
    private final ModificationEngine modificationEngine;
    private final StarShipService starshipService;
    private final ModificationPriceService modificationPriceService;
    private final ModificationPriceEngine modificationPriceEngine;
    private final ProductEngine productEngine;
    private final MoneyEngine moneyEngine;


    public List<ModificationDto> findAll(ModificationFilter modificationFilter, Pageable pageable) {

        return modificationService.findAll(new ModificationSpecification(modificationFilter), pageable)
                .map(modificationMapper::toDto)
                .toList();
    }

    public ModificationDto save(ModificationDto dto) {
        return modificationMapper.toDto(modificationService.save(modificationMapper.toEntity(dto)));
    }

    public void delete(Long modificationId) {
        Modification modification = modificationService.getById(modificationId);

        modificationService.delete(modification);
    }

    public ApplyModificationDto applyModificationStarShip(ApplyModificationDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        Modification modification = modificationService.getById(dto.getModificationId());
        BasicObject item = basicObjectService.getById(dto.getItemId());
//        applyingCheck(modification, item, user);

        StarShip starShip = starshipService.getById(user.getLocation().getId());
        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());

        ModificationPrice modificationPrice = modificationPriceService.getByStationAndModification(station, modification);
        Long moneyAmount = modificationPrice.getPrice().getMoneyAmount();

//        if ((user.getMoney() < moneyAmount) || (!modificationPriceEngine.isEnoughResources(starShip, modificationPrice))) {
//            dto.setIsModified(false);
//            dto.setMessage("There is not enough money or resources to apply modification");
//            return dto;
//        }

//        productEngine.transferProducts(starShip, station, createDto(modificationPrice));
//        moneyEngine.transferMoney(user, station, moneyAmount);
        modificationEngine.applyModification(item, modification);
        dto.setIsModified(true);

        return dto;
    }

    private List<TransferProductsDto> createDto(ModificationPrice modificationPrice) {
        List<TransferProductsDto> dto = new ArrayList<>();
        modificationPrice.getPrice().getPriceProducts()
                .forEach(s -> dto.add(TransferProductsDto.builder()
                        .productId(s.getProduct().getId())
                        .productAmount(s.getProductAmount())
                        .build()));
        return dto;
    }

    private void applyingCheck(Modification modification, BasicObject item, User user) {
        BasicObject starShip = user.getLocation();

        String message = "Can't apply modification. Reason: {}";
        if (!inventoryEngine.isInSpaceTechInventory(starShip, item)) {
            String reason = String.format("Item id = %d is not in SpaceTech inventory. SpaceTech id = %d", item.getId(), user.getLocation().getId());
            log.warn(message, reason);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }

        if (!spaceTechEngine.isUserAtStation(user)) {
            String reason = String.format("User's starship is not docked to any station. User id = %d", user.getId());
            log.warn(message, reason);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }

        if (!modificationEngine.isPossibleToApplyToItem(modification, item)) {
            String reason = String.format("It's impossible to apply modification id = %d to item id = %d -- incompatible types",
                    modification.getId(), item.getId());
            log.warn(message, reason);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }

        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());
        if (!modificationEngine.isStationAbleToModify(modification, station)) {
            String reason = String.format("Station id = %d isn't able to do modification id = %d", station.getId(), modification.getId());
            log.warn(message, reason);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }
    }
}