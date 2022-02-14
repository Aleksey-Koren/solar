package io.solar.repository.inventory.socket;

import io.solar.entity.inventory.socket.SpaceTechSocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import io.solar.entity.objects.BasicObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceTechSocketRepository extends JpaRepository<SpaceTechSocket, Long> {

    Optional<SpaceTechSocket> findByObject(BasicObject object);

    void deleteAllByIdIn(List<Long> socketIds);

}