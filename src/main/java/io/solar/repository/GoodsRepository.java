package io.solar.repository;

import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.objects.BasicObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    Optional<Goods> findByOwnerAndProduct(BasicObject ownew, Product product);
}
