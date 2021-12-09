package io.solar.controller.inventory;

import io.solar.dto.BasicObjectDto;
import io.solar.dto.BasicObjectViewDto;
import io.solar.facade.BasicObjectFacade;
import io.solar.mapper.objects.ObjectViewMapper;
import io.solar.mapper.TotalMapper;
import io.solar.specification.filter.BasicObjectFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping("api/objects/config")
public class ObjectsController {

    private final BasicObjectFacade basicObjectFacade;

    @Autowired
    public ObjectsController(BasicObjectFacade basicObjectFacade) {
        this.basicObjectFacade = basicObjectFacade;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EDIT_INVENTORY')")
    public Page<BasicObjectViewDto> getConfigPage(Pageable pageable, BasicObjectFilter basicObjectFilter) {

        return basicObjectFacade.findAll(pageable, basicObjectFilter);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('EDIT_INVENTORY')")
    @Transactional
    public BasicObjectDto getConfigItem(@PathVariable("id") Long id) {

        return basicObjectFacade.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_INVENTORY')")
    @Transactional
    public BasicObjectDto saveItem(@RequestBody BasicObjectDto basicObjectDto) {

        return basicObjectFacade.save(basicObjectDto);
    }
}
