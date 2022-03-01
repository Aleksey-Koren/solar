package io.solar.dto.inventory.socket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SocketControllerDto {

    private Long objectId;
    private String spaceTechType;
}
