package net.slqmy.castle_siege_plugin.managers;

import com.google.gson.Gson;
import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.arena.CastleSiegeArena;
import net.slqmy.castle_siege_plugin.game.data.CastleSiegeTeamBase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ArenaManager {

    private final List<CastleSiegeArena> arenas = new ArrayList<>();

    public ArenaManager(@NotNull CastleSiegePlugin plugin) {
        File castleSiegeArenaFile = new File(plugin.getDataFolder(), "castle-siege-arenas.json");

        try {
            Reader reader = new FileReader(castleSiegeArenaFile);
            Gson gson = new Gson();

            CastleSiegeArena[] castleSiegeArenas = gson.fromJson(reader, CastleSiegeArena[].class);
            arenas.addAll(List.of(castleSiegeArenas));

            reader.close();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
