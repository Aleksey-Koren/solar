package io.solar.security;

import java.util.Set;

public enum Role {

    USER(Set.of("PLAY_THE_GAME")),
    ADMIN(Set.of("EDIT_PLANET", "EDIT_USER", "SEE_PERMISSIONS", "ASSIGN_PERMISSIONS"));

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }
    private final Set<String> permissions;

    public Set<String> getPermissions() {
        return permissions;
    }
}
