package io.solar.service.inventory.socket;

import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.repository.inventory.socket.SpaceTechSocketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpaceTechSocketService {

    private final SpaceTechSocketRepository spaceTechSocketRepository;

    public SpaceTechSocket getById(Long id) {
        return spaceTechSocketRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no object of %s class with id = %n in database", SpaceTechSocket.class.getSimpleName(), id)));
    }

    public Optional<SpaceTechSocket> findById(Long id) {
        return spaceTechSocketRepository.findById(id);
    }
}