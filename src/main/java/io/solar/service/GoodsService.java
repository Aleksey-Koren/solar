package io.solar.service;

import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Optional<Goods> findById(Long id) {
        return goodsRepository.findById(id);
    }

    public Goods getById(Long id) {
        return goodsRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no %s object with id = %d in database", Goods.class.getSimpleName(), id)
                ));
    }
}
