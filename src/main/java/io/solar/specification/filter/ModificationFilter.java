package io.solar.specification.filter;

import io.solar.entity.modification.ParameterType;
import lombok.Data;

import java.util.List;

@Data
public class ModificationFilter {
    private Long modificationId;
    private String description;
    private Byte minLevel;
    private Byte maxLevel;
    private List<Long> modificationTypeIds;
    private List<ParameterType> parameterTypes;
}
