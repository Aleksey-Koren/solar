package io.solar.mapper;

import io.solar.dto.GoodsDto;
import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.GoodsService;
import io.solar.service.ProductService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodsMapper {

    private final BasicObjectService basicObjectService;
    private final ProductService productService;
    private final GoodsService goodsService;

    public GoodsDto toDto(Goods goods) {
        GoodsDto dto = new GoodsDto();
        dto.setStation(goods.getOwner() != null ? goods.getOwner().getId() : null);
        dto.setProduct(goods.getProduct() != null ? goods.getProduct().getId() : null);
        dto.setAmount(goods.getAmount());
        dto.setPrice(goods.getPrice());
        return dto;
    }

    public Goods toEntity(GoodsDto dto) {
        BasicObject owner = basicObjectService.getById(dto.getStation());
        Product product = productService.getById(dto.getProduct());
        Goods goods;
        Optional<Goods> goodsOpt = goodsService
                .findByOwnerAndProduct(owner, product);
        if(goodsOpt.isPresent()) {
            goods = goodsOpt.get();
        }else{
            goods = new Goods();
            goods.setOwner(owner);
            goods.setProduct(product);
        }
        goods.setAmount(dto.getAmount() != null ? dto.getAmount() : 0);
        goods.setPrice(dto.getPrice() != null ? dto.getPrice() : 0);
        return goods;
    }
}