package net.slqmy.castle_siege_plugin.mobs.base;

import net.minecraft.world.level.Level;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.game.data.region.MapRegion;
import net.slqmy.castle_siege_plugin.mobs.MobType;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

public interface INMSMob extends Listener {
    INMSMob newInstance(Level world, MapRegion region, Game game);
    void prepare();
    boolean isPrepared();
    void spawn();
    void registerEvents();
    Entity getHandle();
    MapRegion getHomeRegion();
    Integer getWeight();
    MobType getType();
}
