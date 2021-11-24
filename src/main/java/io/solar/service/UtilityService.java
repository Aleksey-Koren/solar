package io.solar.service;

import io.solar.entity.Utility;
import io.solar.repository.UtilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilityService {

    private UtilityRepository utilityRepository;

    @Autowired
    public UtilityService(UtilityRepository utilityRepository) {
        this.utilityRepository = utilityRepository;
    }

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

    public boolean deleteByUtilKey(String utilKey) {
        return utilityRepository.deleteByUtilKey(utilKey) == 1;
    }
}