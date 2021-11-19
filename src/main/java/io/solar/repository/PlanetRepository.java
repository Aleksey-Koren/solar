package io.solar.repository;

import io.solar.entity.Planet;
import io.solar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanetRepository extends JpaRepository<Planet, Long>, JpaSpecificationExecutor<Planet> {

}