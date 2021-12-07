package io.solar.specification;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import io.solar.entity.objects.BasicObject_;
import io.solar.specification.filter.StationFilter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class StationSpecification implements Specification<Station> {

    private final StationFilter stationFilter;

    public StationSpecification(StationFilter stationFilter) {
        this.stationFilter = stationFilter;
    }

    @Override
    public Predicate toPredicate(Root<Station> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        if (stationFilter.getPlanetId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(BasicObject_.planet), stationFilter.getPlanetId()));
        }

        if (stationFilter.getPopulationMin() != null) {

            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(BasicObject_.population), stationFilter.getPopulationMin()));
        }

        if (stationFilter.getPopulationMax() != null) {

            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(BasicObject_.population), stationFilter.getPopulationMax()));
        }

        if (Strings.isNotEmpty(stationFilter.getTitle())) {

            predicates.add(criteriaBuilder.like(root.get(BasicObject_.title), stationFilter.getTitle().concat("%")));

        }

        if (Strings.isNotEmpty(stationFilter.getFraction())) {

            predicates.add(criteriaBuilder.like(root.get(BasicObject_.fraction), stationFilter.getFraction().concat("%")));

        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

}


