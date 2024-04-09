package net.slqmy.castle_siege_plugin.managers;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeArenaConfig;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeTeamBaseData.Region;
import net.slqmy.castle_siege_plugin.json.RegionDeserializer;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

public final class ArenaManager {
    private final CastleSiegePlugin plugin;

    @Getter
    private final HashMap<World, CastleSiegeArenaConfig> arenas;

    public ArenaManager() {
        this.plugin = CastleSiegePlugin.getInstance();
        this.arenas = new HashMap<>();
    }

    public void loadFromConfig() {
        File castleSiegeArenaFile = new File(plugin.getDataFolder(), "castle-siege-arenas.json");

        try {
            Reader reader = new FileReader(castleSiegeArenaFile);
            Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Region.class, new RegionDeserializer())
                .create();

            for (CastleSiegeArenaConfig arena : gson.fromJson(reader, CastleSiegeArenaConfig[].class)) {
                arenas.put(Bukkit.getWorld(arena.getWorldName()), arena);
                plugin.getLogger().info(arena.getBases().get(0).getRegion().toString());
            }

            reader.close();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
