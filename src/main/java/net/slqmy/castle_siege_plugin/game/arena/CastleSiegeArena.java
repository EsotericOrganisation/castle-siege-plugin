package net.slqmy.castle_siege_plugin.game.arena;

import lombok.Getter;
import lombok.Setter;
import net.slqmy.castle_siege_plugin.game.data.CastleSiegeTeamBase;
import org.bukkit.Material;

import java.util.List;

@Getter @Setter
public class CastleSiegeArena {
    private List<CastleSiegeTeamBase> bases;
    private List<Object> mineableRegions;
    private List<Object> gates;
    private Material gateMaterial;
    private Material oneWayFenceMaterial;
}
