package io.solar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.solar.entity.Permission;
import io.solar.entity.Planet;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
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