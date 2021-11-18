package io.solar.security;

import java.util.Set;

public enum Role {

    USER(Set.of("PLAY_THE_GAME")),
    ADMIN(Set.of("EDIT_PLANET", "EDIT_USER", "EDIT_PERMISSION", "ASSIGN_PERMISSION", "REVOKE_PERMISSION"));

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }
    private final Set<String> permissions;

    public Set<String> getPermissions() {
        return permissions;
    }
}
