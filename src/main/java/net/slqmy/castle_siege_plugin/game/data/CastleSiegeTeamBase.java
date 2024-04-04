package net.slqmy.castle_siege_plugin.game.data;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;

@Getter @Builder
public class CastleSiegeTeamBase {
    List<Location> spawnPoints;
    List<Location> horseLocations;
    List<Location> cannonLocations;
    List<Location> catapultLocations;
    List<Location> golemLocations;
    Location parrotMailLocation;
    Location attackingCampLocation;
    HashMap<String, Location> npcLocations;
}
