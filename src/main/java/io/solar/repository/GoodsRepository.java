package io.solar.repository;

import io.solar.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods, Goods.Key> {

}
