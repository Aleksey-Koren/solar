package io.solar.dto.modification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModificationTypeDto {
    private Long id;
    private String title;
    private List<ModificationDto> modificationDtoList;
}
