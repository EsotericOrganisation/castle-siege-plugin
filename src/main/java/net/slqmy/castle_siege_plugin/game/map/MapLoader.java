package net.slqmy.castle_siege_plugin.game.map;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.game.data.arena.ArenaConfig;
import net.slqmy.castle_siege_plugin.game.data.npc.NPCData;
import net.slqmy.castle_siege_plugin.game.data.region.StructureRegion;
import net.slqmy.castle_siege_plugin.game.data.team.TeamBaseData;
import net.slqmy.castle_siege_plugin.npc.NPC;
import net.slqmy.castle_siege_plugin.npc.NPCBehaviour;
import net.slqmy.castle_siege_plugin.npc.NPCSpawner;
import net.slqmy.castle_siege_plugin.util.region.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings(value = "unused")
public final class MapLoader {
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final CastleSiegePlugin plugin;

    private final Game game;
    private final ArenaConfig arenaConfig;

    private final MapData mapData;
    private final World world;

    private final Map<Integer, BukkitTask> runnables;

    public MapLoader(Game game) {
        this.plugin = CastleSiegePlugin.getInstance();

        this.game = game;
        this.arenaConfig = game.getArenaConfig();

        this.mapData = new MapData();
        this.world = game.getWorld();

        this.runnables = new HashMap<>();
    }

    public MapLoader loadConfig() {
        mapData.setWindowMaterial(arenaConfig.windowMaterial());
        mapData.setParrotMailEnabled(arenaConfig.parrotMailEnabled());
        mapData.setArtilleryEnabled(arenaConfig.artilleryEnabled());
        mapData.setMonstersEnabled(arenaConfig.monstersEnabled());
        mapData.setTrapsEnabled(arenaConfig.trapsEnabled());
        mapData.setWildTrapsEnabled(arenaConfig.wildTrapsEnabled());
        return this;
    }

    public MapData loadMap() {
        initMapData();
        loadGlobalFeatures();
        loadTeamBases();

        return mapData;
    }

    private void initMapData() {
        mapData.setWindowBlocks(new HashMap<>());
        mapData.setWindowBlockDisplays(new HashMap<>());
        mapData.setNPCsById(new HashMap<>());
    }

    private void loadGlobalFeatures() {
        world.setTime(13000);
    }

    private void loadTeamBases() {
        for (TeamBaseData base : arenaConfig.bases()) {
            mapData.getWindowBlocks().put(base.id(), new ArrayList<>());
            mapData.getWindowBlockDisplays().put(base.id(), new ArrayList<>());

            createWindowBlockDisplays(base);
            spawnNPCs(base);
        }
    }

    private void createWindowBlockDisplays(TeamBaseData base) {
        StructureRegion region = base.region();
        Cuboid.CuboidIterator iterator = region.asCuboid(world).iterator();

        List<Block> windowBlocks = mapData.getWindowBlocks().get(base.id());
        List<BlockDisplay> windowBlockDisplays = mapData.getWindowBlockDisplays().get(base.id());

        AtomicReference<BukkitTask> task = new AtomicReference<>();
        task.set(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!iterator.hasNext()) {
                markRunnableAsComplete(task.get());
                return;
            }

            int iterations = 0;
            while (iterator.hasNext()) {

                if (iterations++ >= iterator.getBlockAmount() / 100)
                    return;

                Block block = iterator.next();

                if (block.getType() == mapData.getWindowMaterial()) {
                    BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(block.getLocation(), EntityType.BLOCK_DISPLAY);
                    blockDisplay.setBlock(block.getBlockData());

                    windowBlocks.add(block);
                    windowBlockDisplays.add(blockDisplay);
                }
            }
        }, 0L, 1L));

        runnables.put(task.get().getTaskId(), task.get());
    }

    private void spawnNPCs(TeamBaseData base) {
        List<NPCData> npcList = base.npcs();

        for (NPCData data : npcList) {
            NPC npc = new NPCSpawner(data, world).spawn();

            Location location = data.getLocation();
            location.setWorld(world);

            npc.setLocation(location);
            npc.setCustomName((MM.deserialize(data.getName())));
            npc.setYawAndPitch(location.getYaw(), location.getPitch());
            npc.setAudience(game.getPlayers());

            npc.sendPacketsToPlayers();

            NPCBehaviour behaviour = new NPCBehaviour(npc, game);
            npc.setOnTick(behaviour::onTick);

            switch (data.getType()) {
                case BLACKSMITH ->
                    npc.setOnInteract(behaviour::blacksmithOnInteract);
                case FLETCHER ->
                    npc.setOnInteract(player -> player.sendMessage("Fletcher: I can help you with your arrows!"));
                case ARTILLERY_GUY ->
                    npc.setOnInteract(player -> player.sendMessage("Artillery Guy: I can help you with your artillery!"));
            }

            mapData.getNPCsById().put(npc.getId(), npc);
        }
    }

    private void markRunnableAsComplete(BukkitTask runnable) {
        runnable.cancel();
        runnables.remove(runnable.getTaskId());

        if (runnables.isEmpty()) {
            game.markAsLoaded();
        }
    }
}
