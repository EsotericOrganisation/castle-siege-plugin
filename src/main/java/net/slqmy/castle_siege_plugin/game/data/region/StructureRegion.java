package net.slqmy.castle_siege_plugin.game.data.region;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.slqmy.castle_siege_plugin.game.data.region.base.IRegion;
import net.slqmy.castle_siege_plugin.util.region.Cuboid;
import org.bukkit.Location;
import org.bukkit.World;

@Getter @Setter @Accessors(chain = true)
public class StructureRegion implements IRegion {
    private Location corner1;
    private Location corner2;
    private boolean ignoreY;

    @Override
    public Cuboid asCuboid(World world) {
        corner1.setWorld(world);
        corner2.setWorld(world);

        Location location1 = corner1.clone();
        Location location2 = corner2.clone();

        if (ignoreY) {
            location1.setY(world.getMinHeight());
            location2.setY(world.getMaxHeight());
        }

        return new Cuboid(location1, location2);
    }
}
