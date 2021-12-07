package io.solar.repository;

import io.solar.entity.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {

    void deleteAllByStationIdIn(List<Long> ids);

}
