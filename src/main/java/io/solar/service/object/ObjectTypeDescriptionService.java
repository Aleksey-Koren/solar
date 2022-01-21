package io.solar.service.object;

import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.repository.ObjectTypeDescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ObjectTypeDescriptionService {

    private final ObjectTypeDescriptionRepository objectTypeDescriptionRepository;

    public Optional<ObjectTypeDescription> findById(Long id) {

        return objectTypeDescriptionRepository.findById(id);
    }

    public ObjectTypeDescription getById(Long id) {
        return objectTypeDescriptionRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no %s object with id = %d in database", ObjectTypeDescription.class.getSimpleName(), id)));
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