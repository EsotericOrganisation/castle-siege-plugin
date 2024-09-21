package net.slqmy.castle_siege_plugin.mobs;

import lombok.Getter;

@Getter
public enum MobType {
    VALLEY_SKELETON("valley_skeleton");

    private final String name;
    MobType(String name) {
        this.name = name;
    }
}
