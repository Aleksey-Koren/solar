package io.solar.specification;

import io.solar.entity.modification.ModificationType;
import io.solar.entity.modification.ModificationType_;
import io.solar.specification.filter.ModificationTypeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ModificationTypeSpecification implements Specification<ModificationType> {

    private final ModificationTypeFilter filter;

    @Override
    public Predicate toPredicate(Root<ModificationType> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getIds() != null) {
            predicates.add(criteriaBuilder.isTrue(root.get(ModificationType_.id).in(filter.getIds())));
        }

        if (filter.getTitle() != null) {
            predicates.add(criteriaBuilder.like(root.get(ModificationType_.title), filter.getTitle() + "%"));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
