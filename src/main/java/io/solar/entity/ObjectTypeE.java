package io.solar.entity;

public enum ObjectTypeE {

    ENGINE("engine"),
    GENERATOR("generator"),
    LARGE_GENERATOR("large_generator"),
    SHIELD("shield"),
    ENERGY_WEAPON("energy_weapon"),
    ROCKET_WEAPON("rocket_weapon"),
    RADAR("radar"),
    ROCKET("rocket"),
    TORPEDO("torpedo"),
    AMMO_WEAPON("ammo_weapon"),
    TURRET("turret"),
    ARMOR("ARMOR"),
    BATTERY("battery"),
    HULL("hull"),
    HULL_ENGINE("huge_engine"),
    CONTAINER("container"),
    PLANET("planet");

    private final String title;

    ObjectTypeE(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}