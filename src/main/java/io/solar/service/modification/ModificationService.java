package io.solar.service.modification;

import io.solar.entity.modification.Modification;
import io.solar.repository.modification.ModificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModificationService {
    private final ModificationRepository modificationRepository;

    public Modification getById(Long modificationId) {

        return modificationRepository.findById(modificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cannot find modification with id = " + modificationId)
                );
    }

    public Optional<Modification> findById(Long modificationId) {

        return modificationRepository.findById(modificationId);
    }
}
