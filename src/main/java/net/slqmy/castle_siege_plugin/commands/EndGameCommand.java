package net.slqmy.castle_siege_plugin.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EndGameCommand implements CommandExecutor {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final CastleSiegePlugin plugin;

    public EndGameCommand() {
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
            player.sendMessage(MM.deserialize("<red>World not found."));
            return true;
        }

        if (!plugin.getGameManager().hasOngoingGame(world)) {
            player.sendMessage(MM.deserialize("<red>There is no ongoing game in this world."));
            return true;
        }

        Game game = plugin.getGameManager().getGames().get(world.getUID());
        if (!game.isLoaded()) {
            player.sendMessage(MM.deserialize("<red>Failed to end the game; Cannot end a game whose map is currently loading!"));
            return true;
        }

        game.endGame((gameWorld) -> {
            player.sendMessage(MM.deserialize("<green>Successfully ended the game and reset the map in world <aqua>" + gameWorld.getName() + "</aqua>!"));
            plugin.getLogger().info("[Termination] Castle Siege game ended by player " + player.getName() + " in world: " + gameWorld.getName());
        });

        return true;
    }
}
