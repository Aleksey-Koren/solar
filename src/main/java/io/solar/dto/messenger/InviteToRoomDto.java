package io.solar.dto.messenger;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InviteToRoomDto {
    private List<Long> invitedIds;
}
