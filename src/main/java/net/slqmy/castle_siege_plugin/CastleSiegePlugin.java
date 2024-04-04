package net.slqmy.castle_siege_plugin;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.customItems.bows.LongBow;
import net.slqmy.castle_siege_plugin.events.PlayerJoinListener;
import net.slqmy.castle_siege_plugin.managers.ArenaManager;
import net.slqmy.castle_siege_plugin.managers.CustomItemManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class CastleSiegePlugin extends JavaPlugin {
    private ArenaManager arenaManager;
    private CustomItemManager customItemManager;

    @Getter
    private static CastleSiegePlugin instance;

    @Override
    public void onLoad() {
        instance = this;
        //arenaManager = new ArenaManager(this);
    }

    @Override
    public void onEnable() {
        customItemManager = new CustomItemManager(this);
        customItemManager.init();

        customItemManager.registerCustomItem(new LongBow(this));
        new PlayerJoinListener(this).register();
    }

}
