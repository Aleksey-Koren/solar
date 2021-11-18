package io.solar.dto;

import io.solar.entity.Planet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String title;
    private String login;
    private String password;
    private Long money;
    private Planet planet;
    private Instant hackBlock;
    private Integer hackAttempts;
    private Set<PermissionDto> permissions;

}