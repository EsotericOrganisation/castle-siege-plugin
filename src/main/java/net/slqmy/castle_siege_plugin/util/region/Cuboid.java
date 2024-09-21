package net.slqmy.castle_siege_plugin.util.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Cuboid implements Iterable<Block>, Cloneable {
    protected final World world;
    protected final int x1, y1, z1;
    protected final int x2, y2, z2;

    private final List<Location> cachedLocations;
    private int cacheIndex;

    public Cuboid(Location corner1, Location corner2) {
        if (!corner1.getWorld().equals(corner2.getWorld())) {
            throw new IllegalArgumentException("Locations must be on the same world");
        }

        this.world = corner1.getWorld();

        this.x1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
        this.y1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
        this.z1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());

        this.x2 = Math.max(corner1.getBlockX(), corner2.getBlockX());
        this.y2 = Math.max(corner1.getBlockY(), corner2.getBlockY());
        this.z2 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        this.cachedLocations = new ArrayList<>();
        this.cacheIndex = 0;
    }

    public Location findRandomSurface(int tries, Predicate<? super Block> condition) {
        while (cacheIndex < cachedLocations.size()) {
            Location location = cachedLocations.get(cacheIndex++);

            if (condition.test(location.getBlock())) {
                return location.add(0.5, 1, 0.5);
            }
        }

        cachedLocations.clear();
        cacheIndex = 0;

        for (int i = 0; i < tries; i++) {
            int x = x1 + (int) (Math.random() * (x2 - x1 + 1));
            int z = z1 + (int) (Math.random() * (z2 - z1 + 1));

            for (int y = y2; y >= y1; y--) {
                Block block = world.getBlockAt(x, y, z);

                if (block.isSolid() && condition.test(block)) {
                    cachedLocations.add(block.getLocation());
                }
            }
        }

        if (cachedLocations.isEmpty()) {
            return null;
        }

        return cachedLocations.get(cacheIndex++).add(0.5, 1, 0.5);
    }

    public boolean contains(Location location) {
        return
            location.getWorld().equals(world) &&
            location.getBlockX() >= x1 && location.getBlockX() <= x2 &&
            location.getBlockY() >= y1 && location.getBlockY() <= y2 &&
            location.getBlockZ() >= z1 && location.getBlockZ() <= z2;
    }

    @Override
    public CuboidIterator iterator() {
        return new CuboidIterator(this);
    }

    @Override
    public Cuboid clone() {
        try {
            return (Cuboid) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class CuboidIterator implements Iterator<Block> {
        private final Cuboid cuboid;
        private int x, y, z;

        public CuboidIterator(Cuboid cuboid) {
            this.cuboid = cuboid;
            this.x = cuboid.x1;
            this.y = cuboid.y1;
            this.z = cuboid.z1;
        }

        public int getBlockAmount() {
            return Math.abs(cuboid.x2 - cuboid.x1) * Math.abs(cuboid.y2 - cuboid.y1) * Math.abs(cuboid.z2 - cuboid.z1);
        }

        @Override
        public boolean hasNext() {
            return x <= cuboid.x2 && y <= cuboid.y2 && z <= cuboid.z2;
        }

        @Override
        public Block next() {
            Block block = cuboid.world.getBlockAt(x, y, z);

            if (++z > cuboid.z2) {
                z = cuboid.z1;

                if (++y > cuboid.y2) {
                    y = cuboid.y1;

                    x++;
                }
            }

            return block;
        }
    }
}
