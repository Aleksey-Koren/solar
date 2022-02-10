package io.solar.security;

import java.util.Set;

import static io.solar.security.PermissionEnum.PLAY_THE_GAME;
import static io.solar.security.PermissionEnum.SEND_ALL_MESSAGE_TYPES;
import static io.solar.security.PermissionEnum.getAllPermissions;

public enum Role {

    USER(Set.of(PLAY_THE_GAME.name())),
    ADMIN(getAllPermissions()),
    SYSTEM_ADMIN(Set.of(PLAY_THE_GAME.name(), SEND_ALL_MESSAGE_TYPES.name()));

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }

    private final Set<String> permissions;

    public Set<String> getPermissions() {
        return permissions;
    }
}
