package io.solar.specification;

import io.solar.entity.User;
import io.solar.entity.User_;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.Room_;
import io.solar.specification.filter.UserFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class UserSpecification implements Specification<User> {

    private final UserFilter filter;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();

        if (filter.getLogin() != null) {
            predicateList.add(criteriaBuilder.like(root.get(User_.login), filter.getLogin() + "%"));
        }

        if (filter.getTitle() != null) {
            predicateList.add(criteriaBuilder.like(root.get(User_.title), filter.getTitle() + "%"));
        }

        if (filter.getNotInRoom() != null) {
            Subquery<User> userSubQuery = query.subquery(User.class);
            Root<User> subQueryRoot = userSubQuery.from(User.class);
            Join<User, Room> userRoomJoin = subQueryRoot.join(User_.rooms);
            userSubQuery.select(subQueryRoot).where(criteriaBuilder.equal(userRoomJoin.get(Room_.id), filter.getNotInRoom()));
            predicateList.add(criteriaBuilder.not(criteriaBuilder.in(root).value(userSubQuery)));
        }

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }
}
