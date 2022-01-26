package io.solar.service;

import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;

    public Optional<Goods> findByOwnerAndProduct(BasicObject owner, Product product) {
        return goodsRepository.findByOwnerAndProduct(owner, product);
    }

    public List<Goods> saveAll(List<Goods> goods) {
        return goodsRepository.saveAll(goods);
    }

    public Goods save(Goods goods) {
        return goodsRepository.save(goods);
    }
}
