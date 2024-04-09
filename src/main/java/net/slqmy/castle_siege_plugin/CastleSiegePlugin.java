package net.slqmy.castle_siege_plugin;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.commands.StartGameCommand;
import net.slqmy.castle_siege_plugin.customItems.bows.LongBow;
import net.slqmy.castle_siege_plugin.customItems.bows.RecurveBow;
import net.slqmy.castle_siege_plugin.events.PlayerJoinListener;
import net.slqmy.castle_siege_plugin.managers.ArenaManager;
import net.slqmy.castle_siege_plugin.managers.CustomItemManager;
import net.slqmy.castle_siege_plugin.managers.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class CastleSiegePlugin extends JavaPlugin {
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private CustomItemManager customItemManager;

    @Getter
    private static CastleSiegePlugin instance;

    public CastleSiegePlugin() {
        instance = this;
    }

    @Override
    public void onLoad() {
        loadResources();
    }

    @Override
    public void onEnable() {
        arenaManager = new ArenaManager();
        arenaManager.loadFromConfig();

        gameManager = new GameManager();

        customItemManager = new CustomItemManager();

        customItemManager.registerCustomItem(new LongBow());
        customItemManager.registerCustomItem(new RecurveBow());
        new PlayerJoinListener().register();

        getCommand("start").setExecutor(new StartGameCommand());
    }

    private void loadResources() {
        if (!getDataFolder().exists()) {
            boolean success = getDataFolder().mkdir();
            getLogger().info("Data folder created: " + success);
        }

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        saveResource("castle-siege-arenas.json", false);
    }

}
