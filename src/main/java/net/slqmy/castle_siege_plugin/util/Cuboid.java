package net.slqmy.castle_siege_plugin.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cuboid implements Iterable<Block>, Cloneable {
    protected final World world;
    protected final int x1, y1, z1;
    protected final int x2, y2, z2;

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
    }

    public boolean contains(Location location) {
        return
            location.getWorld().equals(world) &&
            location.getBlockX() >= x1 && location.getBlockX() <= x2 &&
            location.getBlockY() >= y1 && location.getBlockY() <= y2 &&
            location.getBlockZ() >= z1 && location.getBlockZ() <= z2;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidIterator(this);
    }

    private static class CuboidIterator implements Iterator<Block> {
        private final Cuboid cuboid;
        private final List<Block> blocks;

        private int index = 0;
        public CuboidIterator(Cuboid cuboid) {
            this.cuboid = cuboid;
            this.blocks = new ArrayList<>();

            populateBlockList();
        }

        private void populateBlockList() {
            for (int x = cuboid.x1; x <= cuboid.x2; x++) {
                for (int y = cuboid.y1; y <= cuboid.y2; y++) {
                    for (int z = cuboid.z1; z <= cuboid.z2; z++) {
                        blocks.add(cuboid.world.getBlockAt(x, y, z));
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return index < blocks.size();
        }

        @Override
        public Block next() {
            return blocks.get(index++);
        }
    }
}
