package io.solar.service.modification;

import io.solar.entity.modification.Modification;
import io.solar.repository.modification.ModificationRepository;
import io.solar.specification.ModificationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Modification> findAll(ModificationSpecification specification, Pageable pageable) {

        return modificationRepository.findAll(specification, pageable);
    }

    public Modification save(Modification modification) {
        return modificationRepository.save(modification);
    }

    public void delete(Modification modification) {
        modificationRepository.delete(modification);
    }
}
