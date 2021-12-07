package io.solar.specification.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class StationFilter {
    private Long planetId;
    private Long populationMin;
    private Long populationMax;
    private String fraction;
    private String title;
    private Long productId;
    private

    List<Long> stationsIds;
}
