package io.solar.dto.messenger;

import lombok.Data;

import java.util.List;

@Data
public class CreateRoomDto {

    private Long userId;
    private Boolean isPrivate;
}
