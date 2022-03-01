package io.solar.repository.modification;

import io.solar.entity.modification.ParameterModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterModificationRepository extends JpaRepository<ParameterModification, Long> {
}
