package io.solar.repository;

import io.solar.entity.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionTypeRepository extends JpaRepository<PermissionType, Long> {

}
