package io.solar.dto.modification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModificationDto {
    private Long id;
    private String description;
    private List<ParameterModificationDto> parameterModificationDtoList;
}
