package io.solar.repository.modification;

import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ModificationPrice;
import io.solar.entity.objects.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModificationPriceRepository extends JpaRepository<ModificationPrice, Long>, JpaSpecificationExecutor<ModificationPrice> {

    Optional<ModificationPrice> findByStationAndModification(Station station, Modification modification);

}