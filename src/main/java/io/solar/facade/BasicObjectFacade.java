package io.solar.facade;

import io.solar.dto.BasicObjectDto;
import io.solar.dto.BasicObjectViewDto;
import io.solar.entity.objects.BasicObject;
import io.solar.mapper.object.BasicObjectMapper;
import io.solar.mapper.object.BasicObjectViewMapper;
import io.solar.service.object.BasicObjectService;
import io.solar.specification.BasicObjectSpecification;
import io.solar.specification.filter.BasicObjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class BasicObjectFacade {

    private final BasicObjectService basicObjectService;
    private final BasicObjectMapper basicObjectMapper;
    private final BasicObjectViewMapper basicObjectViewMapper;

    public Page<BasicObjectViewDto> findAll(Pageable pageable, BasicObjectFilter basicObjectFilter) {
        Page<BasicObject> basicObjects = basicObjectService.findAll(new BasicObjectSpecification(basicObjectFilter), pageable);
        return basicObjects.map(basicObjectViewMapper::toDto);
    }


    public BasicObjectDto findById(Long objectId) {
        return basicObjectMapper.toDto(basicObjectService.getById(objectId));
    }

    public BasicObjectDto save(BasicObjectDto basicObjectDto) {

        return basicObjectMapper.toDto(
                basicObjectService.save(
                        basicObjectMapper.toEntity(basicObjectDto)
                )
        );
    }
}
