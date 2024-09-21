package net.slqmy.castle_siege_plugin.mobs.data;

public enum MobEnvironment {
    MOUNTAIN,
    VALLEY,
    MINE,
    WATER,
    COMMON;

    public static MobEnvironment fromString(String string) {
        return
            switch (string.toLowerCase()) {
                case "mountain" -> MOUNTAIN;
                case "valley" -> VALLEY;
                case "mine" -> MINE;
                case "water" -> WATER;
                default -> COMMON;
            };
    }

    public boolean is(MobEnvironment other) {
        return this.equals(other);
    }
}
