package net.slqmy.castle_siege_plugin;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.commands.DebugCommand;
import net.slqmy.castle_siege_plugin.commands.EndGameCommand;
import net.slqmy.castle_siege_plugin.commands.StartGameCommand;
import net.slqmy.castle_siege_plugin.events.bukkit.AsyncChatListener;
import net.slqmy.castle_siege_plugin.events.bukkit.PlayerConnectionListener;
import net.slqmy.castle_siege_plugin.events.packet.PlayerInteractPacketEvent;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.managers.ArenaManager;
import net.slqmy.castle_siege_plugin.managers.GameManager;
import net.slqmy.castle_siege_plugin.managers.PersistentDataManager;
import net.slqmy.castle_siege_plugin.packets.PacketInterceptor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public final class CastleSiegePlugin extends JavaPlugin implements Listener {
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private PersistentDataManager pdcManager;
    private PacketInterceptor packetInterceptor;

    @Getter
    private static CastleSiegePlugin instance;

    public CastleSiegePlugin() {
        CastleSiegePlugin.instance = this;
    }

    @Override
    public void onLoad() {
        loadResources();

        super.onLoad();
    }

    @Override
    public void onEnable() {
        initGameManager();
        initPersistentDataManager();
        initPacketInterceptor();

        registerEvents();
        registerCommands();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        //Avoid concurrent modification
        Map<UUID, Game> games = gameManager.getGames();
        Map<UUID, Game> copy = games.entrySet().stream()
            .filter(entry -> entry.getValue().isLoaded())
            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);

        copy.forEach((id, game) -> games.get(id).endGame(
            (world) -> getLogger().info(
                "[Disabling Logic] Automatically ended Castle Siege game in world: " + world.getName())));

        super.onDisable();
    }

    @EventHandler
    public void onServerLoaded(ServerLoadEvent event) {
        initArenaManager();
    }

    private void loadResources() {
        if (!getDataFolder().exists()) {
            boolean success = getDataFolder().mkdir();
            getLogger().info("Data folder created: " + success);
        }

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        saveResource("castle-siege-arenas.json", true);
    }

    private void initGameManager() {
        this.gameManager = new GameManager();
    }

    private void initPersistentDataManager() {
        this.pdcManager = new PersistentDataManager();
    }

    private void initPacketInterceptor() {
        this.packetInterceptor = new PacketInterceptor();
    }

    private void initArenaManager() {
        this.arenaManager = new ArenaManager();
        arenaManager.loadFromConfig();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(this, this);

        new PlayerConnectionListener().register();
        new AsyncChatListener().register();

        new PlayerInteractPacketEvent().register();
    }

    private void registerCommands() {
        getNotNullCommand("start").setExecutor(new StartGameCommand());
        getNotNullCommand("end").setExecutor(new EndGameCommand());
        getNotNullCommand("debug").setExecutor(new DebugCommand());
    }

    private PluginCommand getNotNullCommand(String name) {
        if (getCommand(name) == null)
            throw new IllegalArgumentException("Command not found: " + name);

        return getCommand(name);
    }

}
