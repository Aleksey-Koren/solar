package io.solar.specification.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketplaceLotFilter {
    private Long ownerId;
    private Long lotId;
    private String objectTypeDescriptionTitle;
    //todo: replace with inventory_type_id list
    private String objectType;
    private String objectTitle;
    private Long userId;
}
