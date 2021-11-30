package io.solar.repository;

import io.solar.entity.objects.ObjectTypeDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectTypeDescriptionRepository extends JpaRepository<ObjectTypeDescription, Long> {

}
