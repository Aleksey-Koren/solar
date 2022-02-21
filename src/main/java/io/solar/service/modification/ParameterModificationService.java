package io.solar.service.modification;

import io.solar.entity.modification.ParameterModification;
import io.solar.repository.modification.ParameterModificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParameterModificationService {

    private final ParameterModificationRepository parameterModificationRepository;

    public ParameterModification getById(Long parameterModificationId) {
        return parameterModificationRepository.findById(parameterModificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cannot find parameter modification with id = " + parameterModificationId)
                );
    }

    public Optional<ParameterModification> findById(Long parameterModificationId) {

        return parameterModificationRepository.findById(parameterModificationId);
    }

}
