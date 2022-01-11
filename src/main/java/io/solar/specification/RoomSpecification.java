package io.solar.specification;

import io.solar.entity.User;
import io.solar.entity.User_;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.RoomType;
import io.solar.entity.messenger.Room_;
import io.solar.entity.messenger.UserRoom;
import io.solar.entity.messenger.UserRoom_;
import io.solar.specification.filter.RoomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RoomSpecification implements Specification<Room> {

    private final RoomFilter roomFilter;

    @Override
    public Predicate toPredicate(Root<Room> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Join<Room, User> myRooms = root.join(Room_.users);

        Join<Room, User> notMyRooms = root.join(Room_.users);

        if (roomFilter.getRoomType() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Room_.type), RoomType.valueOf(roomFilter.getRoomType())));
        }

        predicates.add(criteriaBuilder.equal(myRooms.get(User_.id), roomFilter.getUserId()));

        predicates.add(criteriaBuilder.and(criteriaBuilder.like(notMyRooms.get(User_.title), roomFilter.getTitle() + "%"),
                criteriaBuilder.notEqual(notMyRooms.get(User_.id), roomFilter.getUserId())));

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}