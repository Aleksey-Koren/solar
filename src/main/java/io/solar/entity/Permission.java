package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Permission {
    private Long id;
    private Long userId;
    private Long permissionTypeId;
    private String title;
    private Boolean remove;
}
