package io.solar.service;

import io.solar.entity.Utility;
import io.solar.repository.UtilityRepository;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilityService {

    private final UtilityRepository utilityRepository;

    public Optional<Utility> get(String key) {

        return utilityRepository.findByUtilKey(key);
    }

    public Optional<String> getValue(String key) {

        return utilityRepository.getValue(key);
    }

    public String getValue(String key, String defaultValue) {

        return utilityRepository.getValue(key).orElse(defaultValue);
    }

    public Utility save(Utility utility) {

        return utilityRepository.save(utility);
    }

    public void deleteByUtilKey(String utilKey) {

        utilityRepository.deleteById(utilKey);
    }

    public Utility updateValueByKey(String utilKey, String value) {
        Utility utility = utilityRepository.findByUtilKey(utilKey)
                .orElseThrow(() -> new ServiceException(String.format("Cannot find util key = %s", utilKey)));

        utility.setUtilValue(value);

        return save(utility);
    }
}