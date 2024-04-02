package net.slqmy.castle_siege_plugin.managers;

import com.google.gson.Gson;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.CastleSiegeArena;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class ArenaManager {

    private final List<CastleSiegeArena> arenas;

    private final File castleSiegeArenaFile;

    public ArenaManager(@NotNull CastleSiegePlugin plugin) {
        castleSiegeArenaFile = new File(plugin.getDataFolder(), "castle-siege-arenas.json");

        try {
            Reader reader = new FileReader(castleSiegeArenaFile);

            Gson gson = new Gson();

            arenas = (List<CastleSiegeArena>) gson.fromJson(reader, List.class);

            reader.close();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
