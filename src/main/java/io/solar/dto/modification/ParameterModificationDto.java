package io.solar.dto.modification;

import io.solar.entity.modification.ParameterType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParameterModificationDto {
    private Long id;
    private ParameterType parameterType;
    private Long modificationId;
    private Double modificationValue;
}
