package io.solar.repository.inventory.socket;

import io.solar.entity.inventory.socket.SpaceTechSocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceTechSocketRepository extends JpaRepository<SpaceTechSocket, Long> {

    void deleteAllByIdIn(List<Long> socketIds);

} 