package net.slqmy.castle_siege_plugin.game.data.arena;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@SuppressWarnings("unused")
public final class CastleSiegeTeamBaseData {
    private Region region;
    private Region mineRegion;
    private List<Gate> gates;
    private List<Double[]> spawnPoints;
    private List<Double[]> cannonLocations;
    private List<Double[]> catapultLocations;
    private List<Double[]> ironGolemLocations;
    private List<Double[]> horseLocations;
    private NPCLocations npcLocations;
    private Double[] parrotMailLocation;
    private AttackingCamp attackingCamp;

    @Getter @Setter
    public static class Region {
        private Double[] corner1;
        private Double[] corner2;
        private boolean ignoreY;
    }

    @Getter
    public static class Gate {
        private GateRegion region;
        private double health;
        private boolean singleDecrement;
        private int level;

        @Getter
        public static class GateRegion {
            private Double[] corner1;
            private Double[] corner2WhenOpen;
            private Double[] corner2WhenClosed;
        }
    }

    @Getter
    public static class NPCLocations {
        private Double[] blacksmith;
        private Double[] fletcher;
        private Double[] artilleryGuy;
    }

    @Getter
    public static class AttackingCamp {
        private Region region;
        private Double[] teleporterLocation;
        private double teleporterRadius;
    }
}
