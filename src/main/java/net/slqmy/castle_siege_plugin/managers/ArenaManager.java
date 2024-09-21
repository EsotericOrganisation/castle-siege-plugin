package net.slqmy.castle_siege_plugin.managers;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.deserializer.npc.NPCDataDeserializer;
import net.slqmy.castle_siege_plugin.deserializer.other.ColourDeserializer;
import net.slqmy.castle_siege_plugin.deserializer.other.LocationDeserializer;
import net.slqmy.castle_siege_plugin.deserializer.other.MaterialDeserializer;
import net.slqmy.castle_siege_plugin.deserializer.region.GateRegionDeserializer;
import net.slqmy.castle_siege_plugin.deserializer.region.MapRegionDeserializer;
import net.slqmy.castle_siege_plugin.deserializer.region.StructureRegionDeserializer;
import net.slqmy.castle_siege_plugin.game.data.arena.ArenaConfig;
import net.slqmy.castle_siege_plugin.game.data.npc.NPCData;
import net.slqmy.castle_siege_plugin.game.data.region.GateRegion;
import net.slqmy.castle_siege_plugin.game.data.region.MapRegion;
import net.slqmy.castle_siege_plugin.game.data.region.StructureRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ArenaManager {
    private final CastleSiegePlugin plugin;

    @Getter
    private final Map<UUID, ArenaConfig> arenas;

    public ArenaManager() {
        this.plugin = CastleSiegePlugin.getInstance();
        this.arenas = new HashMap<>();
    }

    public void loadFromConfig() {
        File castleSiegeArenaFile = new File(plugin.getDataFolder(), "castle-siege-arenas.json");

        try {
            Reader reader = new FileReader(castleSiegeArenaFile);
            Gson gson = createGson();

            for (ArenaConfig arena : gson.fromJson(reader, ArenaConfig[].class)) {
                arenas.put(getWorldUID(arena.worldName()), arena);
            }

            reader.close();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Gson createGson() {
        return new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(StructureRegion.class, new StructureRegionDeserializer())
            .registerTypeAdapter(MapRegion.class, new MapRegionDeserializer())
            .registerTypeAdapter(GateRegion.class, new GateRegionDeserializer())
            .registerTypeAdapter(NamedTextColor.class, new ColourDeserializer())
            .registerTypeAdapter(Location.class, new LocationDeserializer())
            .registerTypeAdapter(NPCData.class, new NPCDataDeserializer())
            .registerTypeAdapter(Material.class, new MaterialDeserializer())
            .create();
    }

    private UUID getWorldUID(String worldName) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) plugin.getLogger().warning(
            "The world name provided does not match any existing world. Please double-check castle-siege-arenas.json.");

        assert world != null;
        return world.getUID();
    }
}
