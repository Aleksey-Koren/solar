package io.solar.repository;

import io.solar.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByTitle(String title);
    List<Permission> findByUsersId(Long userId);
}
