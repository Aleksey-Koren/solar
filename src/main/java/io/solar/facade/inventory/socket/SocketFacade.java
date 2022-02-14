package io.solar.facade.inventory.socket;

import io.solar.dto.inventory.socket.EnergyPriorityDto;
import io.solar.dto.inventory.socket.SocketControllerDto;
import io.solar.entity.User;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.service.StarShipService;
import io.solar.service.engine.interfaces.inventory.socket.EnergyEngine;
import io.solar.service.engine.interfaces.inventory.socket.SocketEngine;
import io.solar.service.inventory.socket.SpaceTechSocketService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocketFacade {

    private final StarShipService starShipService;
    private final BasicObjectService basicObjectService;
    private final SpaceTechSocketService spaceTechSocketService;
    private final SocketEngine socketEngine;
    private final EnergyEngine energyEngine;

    public void attachToSocket(SocketControllerDto dto, User user, Long socketId) {
        switch (dto.getSpaceTechType()) {
            case ("STARSHIP") -> attachToStarShip(dto, socketId, user);
            case ("STATION") -> attachToStation(dto, socketId, user);
        }
    }

    public void updateEnergyConsumptionPriority(User user, List<EnergyPriorityDto> energyPriorityDtoList) {
        StarShip starship = starShipService.getById(user.getLocation().getId());

        List<Long> socketIds = starship.getSockets()
                .stream()
                .map(SpaceTechSocket::getId)
                .toList();

        spaceTechSocketService.deleteAllByIds(socketIds);


    }

    private void attachToStarShip(SocketControllerDto dto, Long socketId, User user) {
        StarShip starShip = starShipService.getById(user.getLocation().getId());

        Optional<BasicObject> object = socketEngine.hasAttachedObject(socketId, starShip);
        object.ifPresent(socketEngine::detachFromSocket);

        socketEngine.attachToSocket(socketId, dto.getObjectId());
        energyEngine.recalculateEnergy(starShip);
    }

    private void attachToStation(SocketControllerDto dto, Long socketId, User user) {
        //TODO Implement this method, when Station control will be implemented
    }

}
