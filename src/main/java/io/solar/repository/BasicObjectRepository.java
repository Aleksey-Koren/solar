package io.solar.repository;

import io.solar.entity.objects.BasicObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicObjectRepository extends JpaRepository<BasicObject, Long> {

}