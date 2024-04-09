package net.slqmy.castle_siege_plugin.commands;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeArenaConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartGameCommand implements CommandExecutor {
    private final CastleSiegePlugin plugin;
    public StartGameCommand() {
        this.plugin = CastleSiegePlugin.getInstance();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        String worldName;
        if (args.length == 0) {
            worldName = player.getWorld().getName();
        } else {
            worldName = args[0];
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage("World not found.");
            return true;
        }

        CastleSiegeArenaConfig arena = plugin.getArenaManager().getArenas().get(world);
        Bukkit.getLogger().info(" " + plugin.getArenaManager().getArenas());
        if (arena == null) {
            player.sendMessage("This world is not a minigame world.");
            return true;
        }

        if (plugin.getGameManager().hasOngoingGame(world)) {
            player.sendMessage("There is already an ongoing game in this world.");
            return true;
        }

        plugin.getGameManager().createGame(world, arena).startGame();
        return true;
    }
}
