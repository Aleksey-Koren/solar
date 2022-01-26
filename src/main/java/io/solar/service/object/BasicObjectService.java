package io.solar.service.object;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.repository.BasicObjectRepository;
import io.solar.specification.BasicObjectSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicObjectService {

    private final BasicObjectRepository basicObjectRepository;

    public Optional<BasicObject> findById(Long id) {
        return basicObjectRepository.findById(id);
    }

    public BasicObject getById(Long id) {
        return basicObjectRepository.findById(id).orElseThrow(() ->
                 new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no %s with id = %d in database", BasicObject.class.getSimpleName(), id)
        ));
    }

    public Page<BasicObject> findAll(BasicObjectSpecification basicObjectSpecification, Pageable pageable) {

        return basicObjectRepository.findAll(basicObjectSpecification, pageable);
    }

    public BasicObject save(BasicObject basicObject) {

        return basicObjectRepository.save(basicObject);
    }

    public void deleteByHullId(Long hullId) {

        basicObjectRepository.deleteAllByObjectTypeDescriptionId(hullId);
    }

    public List<BasicObject> findAllById(List<Long> objectIds) {
        return basicObjectRepository.findAllById(objectIds);
    }

    public List<BasicObject> findExactlyAllById(List<Long> objectIds) {
        List<BasicObject> objects = basicObjectRepository.findAllById(objectIds);
        if (objectIds.size() == objects.size()) {
            return objects;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Not all objects exist in database. Expected ids  == %s. But only %s are found",
                            objectIds,
                            objects.stream()
                                    .map(BasicObject::getId)
                                    .toList()
                    ));
        }
    }

    public void deleteAll(List<BasicObject> objects) {
        basicObjectRepository.deleteAll(objects);
    }
}