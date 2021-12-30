package io.solar.security;

import java.util.Set;

public enum Role {

    USER(Set.of("PLAY_THE_GAME")),
    ADMIN(Set.of("PLAY_THE_GAME", "EDIT_PLANET", "EDIT_USER", "SEE_PERMISSIONS", "ASSIGN_PERMISSIONS","EDIT_PRODUCT",
            "EDIT_INVENTORY_TYPE", "EDIT_STATION", "EDIT_INVENTORY")),
    SYSTEM_ADMIN(Set.of("PLAY_THE_GAME", "SYSTEM_ADMIN"));

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }
    private final Set<String> permissions;

    public Set<String> getPermissions() {
        return permissions;
    }
}
