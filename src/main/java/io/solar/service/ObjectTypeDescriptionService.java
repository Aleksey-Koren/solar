package io.solar.service;

import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.repository.ObjectTypeDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectTypeDescriptionService {

    private final ObjectTypeDescriptionRepository objectTypeDescriptionRepository;

    @Autowired
    public ObjectTypeDescriptionService(ObjectTypeDescriptionRepository objectTypeDescriptionRepository) {
        this.objectTypeDescriptionRepository = objectTypeDescriptionRepository;
    }

    public Optional<ObjectTypeDescription> findById(Long id) {

        return objectTypeDescriptionRepository.findById(id);
    }

    public List<ObjectTypeDescription> getAll() {

        return objectTypeDescriptionRepository.findAll();
    }

    public ObjectTypeDescription save(ObjectTypeDescription objectTypeDescription) {

        return objectTypeDescriptionRepository.save(objectTypeDescription);
    }

    public void delete(Long id) {

        objectTypeDescriptionRepository.deleteById(id);
    }

}