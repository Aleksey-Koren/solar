package io.solar.dto.modification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModificationDto {
    private Long id;
    private String description;
    private Byte level;
    private Long modificationTypeId;

    private List<ParameterModificationDto> parameterModificationDtoList;
    private List<Long> availableObjectTypeDescriptionsIds;


}
