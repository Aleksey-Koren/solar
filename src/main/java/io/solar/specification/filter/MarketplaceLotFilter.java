package io.solar.specification.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MarketplaceLotFilter {
    private Long ownerId;
    private Long lotId;
    private Long userId;
    private Long minPrice;
    private Long maxPrice;
    private List<Long> objectTypesIds;
    private String objectTypeDescriptionTitle;
    private String objectTitle;
}
