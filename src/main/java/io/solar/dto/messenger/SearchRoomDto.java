package io.solar.dto.messenger;

import io.solar.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRoomDto extends RoomDtoImpl {
    private List<UserDto> participants;
}
