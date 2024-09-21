package net.slqmy.castle_siege_plugin.mobs.base;

import net.minecraft.world.level.Level;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.game.data.region.MapRegion;
import net.slqmy.castle_siege_plugin.managers.PersistentDataManager;
import net.slqmy.castle_siege_plugin.mobs.MobType;
import org.bukkit.entity.Entity;

public abstract class AbstractCustomMob implements INMSMob {
    protected final CastleSiegePlugin plugin;
    protected final PersistentDataManager pdcManager;

    protected final Game game;
    protected final MapRegion homeRegion;

    protected MobType mobType;
    protected Integer weight;

    protected boolean readyForSpawn;

    public AbstractCustomMob(MapRegion region, Game game) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.pdcManager = plugin.getPdcManager();

        this.game = game;
        this.homeRegion = region;
    }
    public void createMobData(MobType type, Integer weight) {
        this.mobType = type;
        this.weight = weight;
    }

    @Override
    public abstract INMSMob newInstance(Level world, MapRegion region, Game game);
    @Override
    public abstract void registerEvents();
    @Override
    public abstract void prepare();
    @Override
    public abstract void spawn();

    @Override
    public boolean isPrepared() { return readyForSpawn; }

    @Override
    public abstract Entity getHandle();

    @Override
    public MapRegion getHomeRegion() {
        return homeRegion;
    }

    @Override
    public Integer getWeight() {
        return weight;
    }

    @Override
    public MobType getType() {
        return mobType;
    }
}
