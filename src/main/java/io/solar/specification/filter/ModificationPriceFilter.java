package io.solar.specification.filter;

import lombok.Data;

import java.util.List;

@Data
public class ModificationPriceFilter {
    private Long id;
    private List<Long> modificationsIds;
    private List<Long> productsIds;
    private List<Long> stationsIds;
    private List<Long> modificationTypesIds;

    private Long minMoneyAmount;
    private Long maxMoneyAmount;

    private Integer minProductAmount;
    private Integer maxProductAmount;

    private Byte minModificationLevel;
    private Byte maxModificationLevel;
}
