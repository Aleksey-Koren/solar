package io.solar.specification.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ModificationTypeFilter {

    private List<Long> ids;
    private String title;
}
