package io.solar.repository.modification;

import io.solar.entity.modification.Modification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModificationRepository extends JpaRepository<Modification, Long> {
}
