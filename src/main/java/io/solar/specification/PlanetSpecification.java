package io.solar.specification;

import io.solar.entity.Planet;
import io.solar.entity.Planet_;
import io.solar.specification.filter.PlanetFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PlanetSpecification implements Specification<Planet> {

    private final PlanetFilter filter;

    @Override
    public Predicate toPredicate(Root<Planet> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getIds() != null) {
            predicates.add(criteriaBuilder.isTrue(root.get(Planet_.id).in(filter.getIds())));
        }

        if (filter.getTypes() != null) {
            predicates.add(criteriaBuilder.isTrue(root.get(Planet_.type).in(filter.getTypes())));
        }

        if (filter.getParentId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Planet_.planet), filter.getParentId()));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
