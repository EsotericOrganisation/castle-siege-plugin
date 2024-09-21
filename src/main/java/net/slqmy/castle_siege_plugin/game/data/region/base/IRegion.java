package net.slqmy.castle_siege_plugin.game.data.region.base;

import net.slqmy.castle_siege_plugin.util.region.Cuboid;
import org.bukkit.World;

public interface IRegion {
    Cuboid asCuboid(World world);
}
