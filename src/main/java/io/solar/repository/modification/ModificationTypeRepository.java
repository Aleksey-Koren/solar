package io.solar.repository.modification;

import io.solar.entity.modification.ModificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ModificationTypeRepository extends JpaRepository<ModificationType, Long>, JpaSpecificationExecutor<ModificationType> {
}
