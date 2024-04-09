package net.slqmy.castle_siege_plugin.managers;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.game.CastleSiegeGame;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeArenaConfig;
import org.bukkit.World;

import java.util.HashMap;

@Getter
public class GameManager {
    private final HashMap<World, CastleSiegeGame> games = new HashMap<>();
    public GameManager() {}

    public CastleSiegeGame createGame(World world, CastleSiegeArenaConfig arena) {
        games.put(world, new CastleSiegeGame(arena, world.getPlayers()));
        return games.get(world);
    }

    public boolean hasOngoingGame(World world) {
        return games.containsKey(world);
    }
}
