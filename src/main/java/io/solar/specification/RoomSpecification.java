package io.solar.specification;

import io.solar.entity.User;
import io.solar.entity.User_;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.Room_;
import io.solar.entity.messenger.UserRoom;
import io.solar.entity.messenger.UserRoom_;
import io.solar.specification.filter.RoomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RoomSpecification implements Specification<UserRoom> {

    private final RoomFilter roomFilter;

    @Override
    public Predicate toPredicate(Root<UserRoom> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Join<UserRoom, Room> join = root.join(UserRoom_.room);

        ListJoin<Room, User> users = join.joinList(Room_.USERS);

        ListJoin<Room, User> myRooms = users.on(criteriaBuilder.equal(users.get(User_.ID), roomFilter.getUserId()));

        ListJoin<Room, User> notMyRooms = users.on(criteriaBuilder.notEqual(users.get(User_.ID), roomFilter.getUserId()))
                .on(criteriaBuilder.equal(join.get(Room_.IS_PRIVATE), roomFilter.getIsPrivate()));

        predicates.add(criteriaBuilder.like(notMyRooms.get(User_.LOGIN), roomFilter.getTitle() + "%"));

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
