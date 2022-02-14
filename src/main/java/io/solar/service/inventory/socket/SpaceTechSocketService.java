package io.solar.service.inventory.socket;

import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.inventory.socket.SpaceTechSocketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpaceTechSocketService {

    private final SpaceTechSocketRepository spaceTechSocketRepository;

    public SpaceTechSocket getById(Long id) {
        return spaceTechSocketRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no object of %s class with id = %d in database", SpaceTechSocket.class.getSimpleName(), id)));
    }

    public Optional<SpaceTechSocket> findById(Long id) {
        return spaceTechSocketRepository.findById(id);
    }

    public void deleteAllBySpaceTech(BasicObject spaceTech) {
        spaceTechSocketRepository.deleteAllBySpaceTech(spaceTech);
    }

    public void saveAll(List<SpaceTechSocket> spaceTechSocketList) {
        spaceTechSocketRepository.saveAll(spaceTechSocketList);
    }

    public SpaceTechSocket getByObject(BasicObject object) {
        return spaceTechSocketRepository.findByObject(object).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("BasicObject with id = %d is not attached to any slot", object.getId())));
    }
}