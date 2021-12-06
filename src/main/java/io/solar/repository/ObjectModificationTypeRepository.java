package io.solar.repository;

import io.solar.entity.objects.ObjectModificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectModificationTypeRepository extends JpaRepository<ObjectModificationType, Long> {
}
