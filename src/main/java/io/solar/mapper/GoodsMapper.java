package io.solar.mapper;

import io.solar.dto.GoodsDto;
import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.GoodsRepository;
import io.solar.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GoodsMapper {

    private GoodsRepository goodsRepository;
    private BasicObjectRepository basicObjectRepository;
    private ProductRepository productRepository;

    @Autowired
    public GoodsMapper(GoodsRepository goodsRepository,
                       BasicObjectRepository basicObjectRepository,
                       ProductRepository productRepository) {
        this.goodsRepository = goodsRepository;
        this.basicObjectRepository = basicObjectRepository;
        this.productRepository = productRepository;
    }

    public GoodsDto toDto(Goods goods) {
        GoodsDto dto = new GoodsDto();
        dto.setStation(goods.getOwner() != null ? goods.getOwner().getId() : null);
        dto.setProduct(goods.getProduct() != null ? goods.getProduct().getId() : null);
        dto.setAmount(goods.getAmount());
        return dto;
    }

    public Goods toEntity(GoodsDto dto) {
        BasicObject owner = basicObjectRepository.getById(dto.getStation());
        Product product = productRepository.getById(dto.getProduct());
        Goods goods;
        Optional<Goods> goodsOpt = goodsRepository
                .findById(new Goods.Key(owner, product));
        if(goodsOpt.isPresent()) {
            goods = goodsOpt.get();
        }else{
            goods = new Goods();
            goods.setOwner(owner);
            goods.setProduct(product);
        }
        goods.setAmount(dto.getAmount());
        return goods;
    }
}
