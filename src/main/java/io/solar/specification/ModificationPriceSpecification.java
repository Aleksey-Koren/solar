package io.solar.specification;

import io.solar.entity.modification.ModificationPrice;
import io.solar.entity.modification.ModificationPrice_;
import io.solar.entity.modification.Modification_;
import io.solar.entity.price.Price;
import io.solar.entity.price.PriceProduct;
import io.solar.entity.price.PriceProduct_;
import io.solar.entity.price.Price_;
import io.solar.specification.filter.ModificationPriceFilter;
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
public class ModificationPriceSpecification implements Specification<ModificationPrice> {

    private final ModificationPriceFilter filter;

    @Override
    public Predicate toPredicate(Root<ModificationPrice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        ListJoin<Price, PriceProduct> productsPrices = root.join(ModificationPrice_.price).join(Price_.priceProducts);

        //BASIC FIELD FILTER

        if (filter.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(ModificationPrice_.id), filter.getId()));
        }

        if (filter.getMinMoneyAmount() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get(ModificationPrice_.price).get(Price_.moneyAmount), filter.getMinMoneyAmount()
            ));
        }

        if (filter.getMaxMoneyAmount() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get(ModificationPrice_.price).get(Price_.moneyAmount), filter.getMaxMoneyAmount()
            ));
        }

        if (filter.getMinProductAmount() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(productsPrices.get(PriceProduct_.productAmount), filter.getMinProductAmount()));
        }

        if (filter.getMaxProductAmount() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(productsPrices.get(PriceProduct_.productAmount), filter.getMaxProductAmount()));
        }

        if (filter.getMinModificationLevel() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get(ModificationPrice_.modification).get(Modification_.level), filter.getMinModificationLevel()
            ));
        }

        if (filter.getMaxModificationLevel() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get(ModificationPrice_.modification).get(Modification_.level), filter.getMaxModificationLevel()
            ));
        }

        //LIST FILTERS

        if (filter.getModificationsIds() != null) {
            predicates.add(criteriaBuilder.isTrue(root.get(ModificationPrice_.modification).in(filter.getModificationsIds())));
        }

        if (filter.getProductsIds() != null) {
            predicates.add(criteriaBuilder.isTrue(productsPrices.get(PriceProduct_.product).in(filter.getProductsIds())));
        }

        if (filter.getStationsIds() != null) {
            predicates.add(criteriaBuilder.isTrue(root.get(ModificationPrice_.station).in(filter.getStationsIds())));
        }

        if (filter.getModificationTypesIds() != null) {
            predicates.add(criteriaBuilder.isTrue(
                    root.get(ModificationPrice_.modification).get(Modification_.modificationType).in(filter.getModificationsIds())
            ));
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
