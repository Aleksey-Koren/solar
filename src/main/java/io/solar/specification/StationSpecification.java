package io.solar.specification;

import io.solar.entity.*;
import io.solar.entity.objects.Station;
import io.solar.entity.objects.BasicObject_;
import io.solar.entity.objects.Station_;
import io.solar.specification.filter.StationFilter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
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

        if(stationFilter.getStationIds() != null) {
            predicates.add(criteriaBuilder.isTrue(root.get(Station_.id).in(stationFilter.getStationIds())));
        }

        if(stationFilter.getProductId() != null) {
            ListJoin<Station, Goods> join = root.join(Station_.goods);
            predicates.add(criteriaBuilder.equal(join.get(Goods_.product).get(Product_.id), stationFilter.getProductId()));


            if(stationFilter.getGoodsPriceMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(join.get(Goods_.price), stationFilter.getGoodsPriceMax()));
            }
            if(stationFilter.getGoodsPriceMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(join.get(Goods_.price), stationFilter.getGoodsPriceMin()));
            }
            if(stationFilter.getGoodsQuantityMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(join.get(Goods_.amount), stationFilter.getGoodsQuantityMin()));
            }
            if(stationFilter.getGoodsQuantityMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(join.get(Goods_.amount), stationFilter.getGoodsQuantityMax()));
            }
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}