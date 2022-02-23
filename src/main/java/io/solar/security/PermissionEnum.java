package io.solar.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum PermissionEnum {
    PLAY_THE_GAME,
    EDIT_PLANET,
    EDIT_USER,
    SEE_PERMISSIONS,
    ASSIGN_PERMISSIONS,
    REVOKE_PERMISSION,
    EDIT_PRODUCT,
    EDIT_INVENTORY_TYPE,
    EDIT_STATION,
    EDIT_INVENTORY,
    SEND_ALL_MESSAGE_TYPES,
    EDIT_MODIFICATIONS;

    public static Set<String> getAllPermissions() {

        return Arrays.stream(PermissionEnum.values()).map(PermissionEnum::name).collect(Collectors.toSet());
    }
}
