package io.solar.dto;

import io.solar.entity.Permission;
import io.solar.entity.Planet;
import io.solar.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String title;
    private String login;
    private String password;
    private Long money;
    private Planet planet;
    private Instant hackBlock;
    private Integer hackAttempts;
    private Set<Permission> permissions;

}