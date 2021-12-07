package io.solar.specification.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StationFilter {
    private Long planetId;
    private Long populationMin;
    private Long populationMax;
    private String fraction;
    private String title;
}
