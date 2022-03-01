package io.solar.specification;

import io.solar.entity.modification.Modification;
import io.solar.entity.modification.Modification_;
import io.solar.entity.modification.ParameterModification;
import io.solar.entity.modification.ParameterModification_;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.entity.objects.ObjectTypeDescription_;
import io.solar.specification.filter.ModificationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ModificationSpecification implements Specification<Modification> {

    private final ModificationFilter filter;

    @Override
    public Predicate toPredicate(Root<Modification> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getModificationId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(Modification_.id), filter.getModificationId()));
        }

        if (filter.getDescription() != null) {
            predicates.add(criteriaBuilder.like(root.get(Modification_.description), filter.getDescription().concat("%")));
        }

        if (filter.getMinLevel() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Modification_.level), filter.getMinLevel()));
        }

        if (filter.getMaxLevel() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Modification_.level), filter.getMaxLevel()));
        }

        if (filter.getModificationTypeIds() != null) {
            predicates.add(criteriaBuilder.isTrue(root.get(Modification_.modificationType).in(filter.getModificationTypeIds())));
        }

        if (filter.getParameterTypes() != null) {
            ListJoin<Modification, ParameterModification> parameterModificationJoin = root.join(Modification_.parameterModifications);
            predicates.add(criteriaBuilder.isTrue(
                    parameterModificationJoin.get(ParameterModification_.parameterType).in(filter.getParameterTypes())
            ));
        }

        if (filter.getOTDIds() != null) {
            ListJoin<Modification, ObjectTypeDescription> OTDJoin = root.join(Modification_.availableObjectTypeDescriptions);
            predicates.add(criteriaBuilder.isTrue(OTDJoin.get(ObjectTypeDescription_.id).in(filter.getOTDIds())));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}