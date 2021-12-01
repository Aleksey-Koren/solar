package io.solar.repository;

import io.solar.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilityRepository extends JpaRepository<Utility, String> {

    Optional<Utility> findByUtilKey(String key1);

    @Query("Select u.utilValue from Utility u where u.utilKey = ?1")
    Optional<String> getValue(String key);
}