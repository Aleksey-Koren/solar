package io.solar.specification;

import io.solar.entity.Planet;
import io.solar.entity.Planet_;
import io.solar.specification.filter.PlanetFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class PlanetSpecification implements Specification<Planet>{

    private PlanetFilter filter;

    public PlanetSpecification(PlanetFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Planet> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        if(filter.getIds() != null) {
            predicates.add(criteriaBuilder.isTrue(root.get(Planet_.id).in(filter.getIds())));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
