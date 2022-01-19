package io.solar.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {

    private String token;
    private String newPassword;
}
