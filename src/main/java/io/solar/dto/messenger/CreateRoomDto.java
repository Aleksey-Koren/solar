package io.solar.dto.messenger;

import lombok.Data;

import java.util.List;

@Data
public class CreateRoomDto {

    private List<Long> userId;
    private boolean isPrivate;
}
