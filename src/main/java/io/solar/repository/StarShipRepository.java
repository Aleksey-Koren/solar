package io.solar.repository;

import io.solar.entity.objects.StarShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StarShipRepository extends JpaRepository<StarShip, Long> {

}
