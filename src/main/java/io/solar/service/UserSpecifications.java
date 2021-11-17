package io.solar.service;

import io.solar.entity.User;
import io.solar.entity.User_;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    public static Specification<User> loginStartsWith(String login) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(User_.login), login + "%");
    }

    public static Specification<User> titleStartsWith(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(User_.title), title + "%");
    }
}
