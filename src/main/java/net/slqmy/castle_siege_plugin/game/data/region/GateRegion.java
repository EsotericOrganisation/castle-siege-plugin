package net.slqmy.castle_siege_plugin.game.data.region;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.slqmy.castle_siege_plugin.game.data.region.base.IRegion;
import net.slqmy.castle_siege_plugin.util.region.Cuboid;
import org.bukkit.Location;
import org.bukkit.World;

@Getter @Setter @Accessors(chain = true)
public class GateRegion implements IRegion {
    private Location corner1;
    private Location corner2WhenOpen;
    private Location corner2WhenClosed;

    @Override
    public Cuboid asCuboid(World world) {
        corner1.setWorld(world);
        corner2WhenClosed.setWorld(world);

        return new Cuboid(corner1, corner2WhenClosed);
    }

    public Cuboid asCuboidWhenOpen(World world) {
        corner1.setWorld(world);
        corner2WhenOpen.setWorld(world);

        return new Cuboid(corner1, corner2WhenOpen);
    }
}
