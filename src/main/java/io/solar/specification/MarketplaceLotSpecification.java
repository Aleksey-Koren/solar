package io.solar.specification;

import io.solar.entity.marketplace.MarketplaceBet_;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.entity.marketplace.MarketplaceLot_;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.BasicObject_;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.entity.objects.ObjectTypeDescription_;
import io.solar.specification.filter.MarketplaceLotFilter;
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
public class MarketplaceLotSpecification implements Specification<MarketplaceLot> {

    private final MarketplaceLotFilter filter;

    @Override
    public Predicate toPredicate(Root<MarketplaceLot> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Join<BasicObject, ObjectTypeDescription> objectTypeDescriptionJoin = root.join(MarketplaceLot_.object).join(BasicObject_.objectTypeDescription);

        if (filter.getOwnerId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(MarketplaceLot_.owner), filter.getOwnerId()));
        }

        if (filter.getLotId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(MarketplaceLot_.id), filter.getLotId()));
        }

        if (filter.getUserId() != null) {
            predicates.add(criteriaBuilder.equal(root.join(MarketplaceLot_.bets).get(MarketplaceBet_.user), filter.getUserId()));
        }

        if (filter.getMinPrice() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(MarketplaceLot_.instantPrice), filter.getMinPrice()));
        }

        if (filter.getMaxPrice() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(MarketplaceLot_.instantPrice), filter.getMaxPrice()));
        }

        if (filter.getObjectTypesIds() != null) {
            predicates.add(criteriaBuilder.isTrue(objectTypeDescriptionJoin.get(ObjectTypeDescription_.inventoryType).in(filter.getObjectTypesIds())));
        }

        if (filter.getObjectTypeDescriptionTitle() != null) {
            predicates.add(criteriaBuilder.like(objectTypeDescriptionJoin.get(ObjectTypeDescription_.title), filter.getObjectTypeDescriptionTitle() + "%"));
        }

        if (filter.getObjectTitle() != null) {
            predicates.add(criteriaBuilder.like(root.get(MarketplaceLot_.object).get(BasicObject_.title), filter.getObjectTitle() + "%"));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
