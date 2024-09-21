package net.slqmy.castle_siege_plugin.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.game.data.arena.ArenaConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartGameCommand implements CommandExecutor {
    private static final MiniMessage MM = MiniMessage.miniMessage();
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
            player.sendMessage(MM.deserialize("<red>World not found."));
            return true;
        }

        ArenaConfig arena = plugin.getArenaManager().getArenas().get(world.getUID());
        if (arena == null) {
            player.sendMessage(MM.deserialize("This world is not a minigame world; No matching config in <light_purple>castle-siege-arenas.json</light_purple>."));
            return true;
        }

        if (plugin.getGameManager().hasOngoingGame(world)) {
            player.sendMessage(MM.deserialize("There is already an ongoing game in this world."));
            return true;
        }

        Game game = plugin.getGameManager().createGame(world, arena);
        game.startGame((gameWorld) -> {
            player.sendMessage(MM.deserialize("<green>Your Castle Siege game has finished loading in world: <aqua>" + gameWorld.getName() + "</aqua>!"));
            plugin.getLogger().info("[Game Loaded] Castle Siege game loaded in world: " + gameWorld.getName());
        });

        return true;
    }
}
