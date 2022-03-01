package io.solar.service.modification;

import io.solar.entity.modification.ModificationType;
import io.solar.repository.modification.ModificationTypeRepository;
import io.solar.specification.ModificationTypeSpecification;
import io.solar.specification.filter.ModificationTypeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModificationTypeService {

    private final ModificationTypeRepository modificationTypeRepository;

    public ModificationType getById(Long modificationTypeId) {

        return modificationTypeRepository.findById(modificationTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find modification type with id = " + modificationTypeId));
    }

    public Optional<ModificationType> findById(Long modificationTypeId) {

        return modificationTypeRepository.findById(modificationTypeId);
    }

    public Page<ModificationType> findAll(Pageable pageable, ModificationTypeFilter filter) {
        return modificationTypeRepository.findAll(new ModificationTypeSpecification(filter), pageable);
    }

    public ModificationType save(ModificationType modificationType) {
        return modificationTypeRepository.save(modificationType);
    }



    public void deleteById(Long id) {
        modificationTypeRepository.deleteById(id);
    }
}
