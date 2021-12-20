package io.solar.controller;

import io.solar.dto.ProductDto;
import io.solar.facade.ProductFacade;
import io.solar.service.ProductService;
import io.solar.utils.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/product")
public class ProductController {

    private final ProductFacade productFacade;
    private final ProductService productService;

    @Transactional
    @PreAuthorize("hasAuthority('EDIT_PRODUCT')")
    @PostMapping
    public ResponseEntity<ProductDto> save(@RequestBody ProductDto dto) {
        return ResponseEntity.ok().body(productFacade.save(dto));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_PRODUCT')")
    @GetMapping
    public Page<ProductDto> getAll(@PageableDefault Pageable pageable) {
        return productFacade.findAll(pageable);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_PRODUCT')")
    @GetMapping("{id}")
    public ResponseEntity<ProductDto> get(@PathVariable("id") Long id) {
        Optional<ProductDto> dto = productFacade.findById(id);
        return dto.isPresent() ? ResponseEntity.ok(dto.get()) : ResponseEntity.notFound().build();
    }

    @Transactional
    @GetMapping("utils/dropdown")
    public List<Option> dropdown() {
        return productService.findAll()
                .stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(toList());
    }

    @Transactional
    @PreAuthorize("hasAuthority('EDIT_PRODUCT')")
    @DeleteMapping(value = "{id}")
    public void delete(@PathVariable("id") Long id) {
        productService.deleteById(id);
    }
}
