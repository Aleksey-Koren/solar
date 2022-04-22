package io.solar.specification.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PlanetFilter {
    private List<Long> ids;
    private String[] types;
    private Long parentId;
}
