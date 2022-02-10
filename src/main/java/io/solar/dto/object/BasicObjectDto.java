package io.solar.dto.object;

import io.solar.dto.inventory.InventorySocketDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BasicObjectDto extends BasicObjectViewDto {

    private List<BasicObjectViewDto> attachedObjects;
    private List<InventorySocketDto> socketList;
}
