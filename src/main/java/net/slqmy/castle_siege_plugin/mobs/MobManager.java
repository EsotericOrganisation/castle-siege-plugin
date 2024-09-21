package net.slqmy.castle_siege_plugin.mobs;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.game.data.region.MapRegion;
import net.slqmy.castle_siege_plugin.game.data.team.TeamBaseData;
import net.slqmy.castle_siege_plugin.mobs.base.INMSMob;
import net.slqmy.castle_siege_plugin.mobs.data.MobEnvironment;
import net.slqmy.castle_siege_plugin.util.NMSUtil;
import net.slqmy.castle_siege_plugin.util.interfaces.ITickable;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.*;

public class MobManager implements ITickable {
    private static final Random random = new Random();

    private final List<INMSMob> registeredMobs;
    private final Game game;
    private final World world;

    @Getter
    private final List<Entity> trackedMobs;
    @Getter
    private final List<MapRegion> mountains;
    @Getter
    private final List<MapRegion> valleys;
    @Getter
    private final List<MapRegion> mines;
    @Getter
    private final List<List<MapRegion>> regionList;

    private Map<MobEnvironment, List<INMSMob>> cachedMobs;
    private Map<MobEnvironment, Integer> cachedWeights;

    public MobManager(Game game) {
        this.registeredMobs = new ArrayList<>();
        this.trackedMobs = new ArrayList<>();

        this.game = game;
        this.world = game.getWorld();

        this.mountains = game.getArenaConfig().globalRegions().mountains();
        this.valleys = game.getArenaConfig().globalRegions().valleys();
        this.mines = game.getArenaConfig().bases().stream().map(TeamBaseData::mineRegion).toList();
        this.regionList = List.of(mountains, valleys, mines);
    }

    public void cacheData() {
        cachedMobs = cacheMobs();
        cachedWeights = cacheWeights();
    }

    @Override
    public void tick() {
        for (List<MapRegion> regions : regionList) {
            regions.forEach(this::attemptSpawnMob);
        }
    }

    private HashMap<MobEnvironment, List<INMSMob>> cacheMobs() {
        HashMap<MobEnvironment, List<INMSMob>> mobs = new HashMap<>();

        for (INMSMob mob : registeredMobs) {
            MobEnvironment environment = mob.getHomeRegion().getEnvironment();

            if (mobs.containsKey(environment)) {
                mobs.get(environment).add(mob);
            } else {
                mobs.put(environment, new ArrayList<>(List.of(mob)));
            }
        }

        return mobs;
    }

    private HashMap<MobEnvironment, Integer> cacheWeights() {
        HashMap<MobEnvironment, Integer> weights = new HashMap<>();

        cachedMobs.forEach((environment, mobs) ->
            weights.put(environment, mobs.stream().mapToInt(INMSMob::getWeight).sum()));

        return weights;
    }

    private void attemptSpawnMob(MapRegion region) {
        double rate = region.getMobSpawnRate();
        if (rate <= 0) return;

        if (random.nextDouble() > rate / 100) return;

        int cap = region.getMobCap();
        if (cap <= 0 || region.getTotalMobs() >= cap) return;

        List<INMSMob> regionMobs = cachedMobs.get(region.getEnvironment());
        if (regionMobs == null || regionMobs.isEmpty()) return;

        INMSMob mob = getWeightedRandomMob(region.getEnvironment())
            .newInstance(NMSUtil.toNMSWorld(world), region, game);

        mob.prepare();
        if (!mob.isPrepared()) return;

        mob.spawn();

        trackedMobs.add(mob.getHandle());
        region.incrementTotalMobs();
    }

    private INMSMob getWeightedRandomMob(MobEnvironment environment) {
        int randomInt = random.nextInt(cachedWeights.get(environment));

        for (INMSMob mob : cachedMobs.get(environment)) {
            randomInt -= mob.getWeight();
            if (randomInt <= 0) return mob;
        }

        throw new IllegalStateException("No mob was selected; Given list was empty.");
    }

    public void registerMob(INMSMob mob) {
        registeredMobs.add(mob);
        mob.registerEvents();
    }
}
