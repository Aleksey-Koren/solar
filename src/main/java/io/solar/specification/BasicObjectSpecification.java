package io.solar.specification;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.BasicObject_;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.entity.objects.ObjectTypeDescription_;
import io.solar.specification.filter.BasicObjectFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;

public class BasicObjectSpecification implements Specification<BasicObject> {

    private final BasicObjectFilter basicObjectFilter;

    public BasicObjectSpecification(BasicObjectFilter basicObjectFilter) {
        this.basicObjectFilter = basicObjectFilter;
    }

    @Override
    public Predicate toPredicate(Root<BasicObject> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        Join<BasicObject, ObjectTypeDescription> join = root.join(BasicObject_.objectTypeDescription);

        if (TRUE.equals(basicObjectFilter.getDetached())) {
            predicates.add(criteriaBuilder.isNull(root.get(BasicObject_.attachedToShip)));
            predicates.add(criteriaBuilder.isNull(root.get(BasicObject_.attachedToSocket)));
        }

        if (basicObjectFilter.getInventoryType() != null) {
            predicates.add(criteriaBuilder.equal(join.get(ObjectTypeDescription_.inventoryTypeId), basicObjectFilter.getInventoryType()));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
