package net.slqmy.castle_siege_plugin.game.map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.slqmy.castle_siege_plugin.npc.NPC;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;

import java.util.List;
import java.util.Map;

@Getter @Setter @Accessors(chain = true)
public final class MapData {
    private Material windowMaterial;
    private boolean parrotMailEnabled;
    private boolean artilleryEnabled;
    private boolean monstersEnabled;
    private boolean trapsEnabled;
    private boolean wildTrapsEnabled;

    private Map<String, List<Block>> windowBlocks;
    private Map<String, List<BlockDisplay>> windowBlockDisplays;
    private Map<Integer, NPC> NPCsById;
}
