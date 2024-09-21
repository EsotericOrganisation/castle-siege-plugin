package net.slqmy.castle_siege_plugin.game.data.npc;

import lombok.Getter;

@Getter
public enum NPCType {
    BLACKSMITH("blacksmith"),
    FLETCHER("fletcher"),
    ARTILLERY_GUY("artillery_guy");

    private final String id;

    NPCType(String id) {
        this.id = id;
    }

    public static NPCType fromString(String str) {
        for (NPCType type : values()) {
            if (type.id.equals(str))
                return type;
        }

        return null;
    }
}
