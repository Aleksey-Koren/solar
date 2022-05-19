package io.solar.specification;

import io.solar.entity.User;
import io.solar.entity.User_;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.RoomType;
import io.solar.entity.messenger.Room_;
import io.solar.specification.filter.RoomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RoomSpecification implements Specification<Room> {

    private final RoomFilter roomFilter;

    @Override
    public Predicate toPredicate(Root<Room> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        Join<Room, User> usersInRoom = root.join(Room_.users);

        Join<Room, User> usersInRoom2 = root.join(Room_.users);


        if (roomFilter.getRoomType() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Room_.type), RoomType.valueOf(roomFilter.getRoomType())));
        }

        predicates.add(criteriaBuilder.equal(usersInRoom.get(User_.id), roomFilter.getUserId()));

        predicates.add(criteriaBuilder.and(criteriaBuilder.like(usersInRoom2.get(User_.title), roomFilter.getTitle() + "%"),
                criteriaBuilder.notEqual(usersInRoom2.get(User_.id), roomFilter.getUserId())));

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
