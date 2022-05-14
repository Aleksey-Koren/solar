package io.solar.dto;

import io.solar.dto.object.BasicObjectViewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

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
    private Long locationId;
    private Instant hackBlock;
    private Integer hackAttempts;
    private String avatar;
    private Integer emailNotifications;
    private List<PermissionDto> permissions;
}