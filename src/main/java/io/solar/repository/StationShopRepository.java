package io.solar.repository;

import io.solar.entity.shop.StationShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationShopRepository extends JpaRepository<StationShop, Long> {

    Optional<StationShop> findByStationId(Long stationId);

}
