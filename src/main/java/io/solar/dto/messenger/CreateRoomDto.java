package io.solar.dto.messenger;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateRoomDto {

    private Long userId;
    private Boolean isPrivate;
    private String title;
}
