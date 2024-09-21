package net.slqmy.castle_siege_plugin.game.data.team;

import net.kyori.adventure.text.format.NamedTextColor;
import net.slqmy.castle_siege_plugin.game.data.npc.NPCData;
import net.slqmy.castle_siege_plugin.game.data.region.GateRegion;
import net.slqmy.castle_siege_plugin.game.data.region.MapRegion;
import net.slqmy.castle_siege_plugin.game.data.region.StructureRegion;
import org.bukkit.Location;

import java.util.List;

public record TeamBaseData(
    String id,
    String name,
    NamedTextColor color,
    StructureRegion region,
    MapRegion mineRegion,
    List<Gate> gates,
    List<Location> spawnPoints,
    List<Location> cannonLocations,
    List<Location> catapultLocations,
    List<Location> ironGolemLocations,
    List<Location> horseLocations,
    List<NPCData> npcs,
    Location parrotMailLocation,
    AttackingCamp attackingCamp
) {
    public record Gate(
        GateRegion region,
        double health,
        boolean singleDecrement,
        int level
    ) {}

    public record AttackingCamp(
        StructureRegion region,
        Location teleporterLocation,
        double teleporterRadius
    ) {}
}
