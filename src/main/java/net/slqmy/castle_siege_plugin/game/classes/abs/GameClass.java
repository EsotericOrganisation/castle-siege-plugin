package net.slqmy.castle_siege_plugin.game.classes.abs;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.Game;
import org.bukkit.entity.Player;

public abstract class GameClass {
    protected final CastleSiegePlugin plugin;
    protected final Game game;

    public GameClass(Game game) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.game = game;
    }

    public int getPlayerCount(int totalPlayerCount) {
        return 0;
    }

    public void equipPlayer(Player player) {
        player.getInventory().clear();
    }

    public abstract void sendInfo(Player player);
    public abstract GameClass newInstance();

}
