package io.solar.dto.object;

import io.solar.dto.BasicObjectViewDto;
import io.solar.dto.GoodsDto;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class StarShipDto extends BasicObjectViewDto {
    private List<GoodsDto> goods;
    private Long userId;
}