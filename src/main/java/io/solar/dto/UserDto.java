package io.solar.dto;

import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String title;
    private String login;
    private String password;
    private Long money;
    private BasicObject location;
    private Instant hackBlock;
    private Integer hackAttempts;
    private Set<PermissionDto> permissions;
}