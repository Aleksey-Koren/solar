package io.solar.dto.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StarshipObjectsDto {
    private Long starshipId;
    private List<Long> objectsIdsToMove;
}
