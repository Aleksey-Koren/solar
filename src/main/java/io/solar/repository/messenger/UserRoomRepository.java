package io.solar.repository.messenger;

import io.solar.entity.messenger.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, UserRoom.UserRoomPK>, JpaSpecificationExecutor<UserRoom> {

}