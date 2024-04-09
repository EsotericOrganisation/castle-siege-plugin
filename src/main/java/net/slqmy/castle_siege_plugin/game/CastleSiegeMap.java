package net.slqmy.castle_siege_plugin.game;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeArenaConfig;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeTeamBaseData;
import net.slqmy.castle_siege_plugin.util.Cuboid;
import net.slqmy.castle_siege_plugin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CastleSiegeMap {
    private final CastleSiegeArenaConfig arenaConfig;
    private final CastleSiegeGame game;

    private Material windowMaterial;
    private boolean parrotMailEnabled;
    private boolean artilleryEnabled;
    private boolean monstersEnabled;
    private boolean trapsEnabled;
    private boolean wildTrapsEnabled;

    @Getter
    private final HashMap<CastleSiegeTeamBaseData, List<Block>> windowBlocks;
    public CastleSiegeMap(CastleSiegeArenaConfig arenaConfig, CastleSiegeGame game) {
        this.arenaConfig = arenaConfig;
        this.game = game;

        this.windowBlocks = new HashMap<>();
    }

    public CastleSiegeMap loadConfig() {
        windowMaterial = Material.getMaterial(arenaConfig.getWindowMaterial());
        parrotMailEnabled = arenaConfig.isParrotMailEnabled();
        artilleryEnabled = arenaConfig.isArtilleryEnabled();
        monstersEnabled = arenaConfig.isMonstersEnabled();
        trapsEnabled = arenaConfig.isTrapsEnabled();
        wildTrapsEnabled = arenaConfig.isWildTrapsEnabled();
        return this;
    }

    public CastleSiegeMap loadMap() {
        loadGlobalFeatures();
        loadTeamBases();
        return this;
    }

    private void loadGlobalFeatures() {

    }

    private void loadTeamBases() {
        Bukkit.getLogger().info(arenaConfig.getBases().size() + "");
        for (CastleSiegeTeamBaseData base : arenaConfig.getBases()) {
            // Load the one-way blocks for each base
            windowBlocks.put(base, new ArrayList<>());
            setWindowsAndGates(base);
        }
    }

    private void setWindowsAndGates(CastleSiegeTeamBaseData base) {
        CastleSiegeTeamBaseData.Region region = base.getRegion();
        World world = game.getWorld();

        Bukkit.getLogger().info(region.getCorner1()[0] + " " + region.getCorner1()[1] + " " + region.getCorner1()[2] + " " + region.getCorner2()[0] + " " + region.getCorner2()[1] + " " + region.getCorner2()[2]);
        Location corner1 = Util.toLocation(region.getCorner1(), world);
        Location corner2 = Util.toLocation(region.getCorner2(), world);

        if (region.isIgnoreY()) {
            corner1.setY(world.getMinHeight());
            corner2.setY(world.getMaxHeight());
        }

        Cuboid cuboid = new Cuboid(corner1, corner2);
        for (Block block : cuboid) {
            if (block.getType().equals(windowMaterial)) {
                windowBlocks.get(base).add(block);

                BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(block.getLocation(), EntityType.BLOCK_DISPLAY);
                blockDisplay.setBlock(block.getBlockData());
            }
        }
    }
}
