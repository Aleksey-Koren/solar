package io.solar.facade;

import io.solar.dto.BasicObjectDto;
import io.solar.dto.BasicObjectViewDto;
import io.solar.entity.objects.BasicObject;
import io.solar.mapper.objects.BasicObjectMapper;
import io.solar.mapper.objects.BasicObjectViewMapper;
import io.solar.service.object.BasicObjectService;
import io.solar.specification.BasicObjectSpecification;
import io.solar.specification.filter.BasicObjectFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BasicObjectFacade {

    private final BasicObjectService basicObjectService;
    private final BasicObjectMapper basicObjectMapper;
    private final BasicObjectViewMapper basicObjectViewMapper;

    @Autowired
    public BasicObjectFacade(BasicObjectService basicObjectService,
                             BasicObjectMapper basicObjectMapper,
                             BasicObjectViewMapper basicObjectViewMapper) {

        this.basicObjectService = basicObjectService;
        this.basicObjectMapper = basicObjectMapper;
        this.basicObjectViewMapper = basicObjectViewMapper;

    }

    public Page<BasicObjectViewDto> findAll(Pageable pageable, BasicObjectFilter basicObjectFilter) {
        Page<BasicObject> basicObjects = basicObjectService.findAll(new BasicObjectSpecification(basicObjectFilter), pageable);
        return basicObjects.map(basicObjectViewMapper::toDto);
    }


    public BasicObjectDto findById(Long objectId) {

        BasicObject basicObject = basicObjectService.findById(objectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find object with id = %d", objectId)));

        return basicObjectMapper.toDto(basicObject);
    }

    public BasicObjectDto save(BasicObjectDto basicObjectDto) {

        return basicObjectMapper.toDto(
                basicObjectService.save(
                        basicObjectMapper.toEntity(basicObjectDto)
                )
        );
    }
}
