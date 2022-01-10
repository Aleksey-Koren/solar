package io.solar.dto;

import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.entity.objects.BasicObject;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String title;
    private String login;
    private String email;
    private String password;
    private Long money;
    private BasicObjectViewDto location;
    private Instant hackBlock;
    private Integer hackAttempts;
    private String avatar;
    private Integer emailNotifications;
    private Set<PermissionDto> permissions;
    private List<RoomDtoImpl> rooms;
}