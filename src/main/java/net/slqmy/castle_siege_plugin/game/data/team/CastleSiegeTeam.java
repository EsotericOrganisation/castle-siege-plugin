package net.slqmy.castle_siege_plugin.game.data.team;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.CastleSiegeGame;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeTeamBaseData;
import net.slqmy.castle_siege_plugin.game.data.player.TeamPlayer;
import net.slqmy.castle_siege_plugin.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public final class CastleSiegeTeam {
    private final static Random random = new Random();

    private final CastleSiegePlugin plugin;
    private final CastleSiegeGame game;
    private final CastleSiegeTeamBaseData baseData;
    private final List<TeamPlayer> teamPlayers;
    private final List<LivingEntity> teamEntities;

    public CastleSiegeTeam(CastleSiegeTeamBaseData baseData, CastleSiegeGame game) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.baseData = baseData;
        this.teamPlayers = new ArrayList<>();
        this.teamEntities = new ArrayList<>();
        this.game = game;

        startTasks();
    }

    private void startTasks() {
        List<Block> windowBlocks = game.getMap().getWindowBlocks().get(baseData);
        new OneWayBlockHandler(windowBlocks).runTaskTimer(plugin, 0L, 1L);
    }

    public void addPlayer(Player player) {
        teamPlayers.add(new TeamPlayer(player, this));
        teamEntities.add(player);
    }

    public void removePlayer(Player player) {
        teamPlayers.removeIf(teamPlayer -> teamPlayer.getPlayer().equals(player));
        teamEntities.remove(player);
    }

    public void spawnPlayers() {
        List<Location> spawnPoints = Util.toLocations(baseData.getSpawnPoints(), game.getWorld());

        for (TeamPlayer teamPlayer : teamPlayers) {
            teamPlayer.getPlayer().teleport(spawnPoints.get(random.nextInt(spawnPoints.size())));
            teamPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    private final class OneWayBlockHandler extends BukkitRunnable {
        private final List<Block> windowBlocks;
        public OneWayBlockHandler(List<Block> windowBlocks) {
            this.windowBlocks = windowBlocks;
        }
        @Override
        public void run() {
            for (Block block : windowBlocks) {
                for (TeamPlayer teamPlayer : CastleSiegeTeam.this.teamPlayers) {
                    Player player = teamPlayer.getPlayer();

                    if (player.getLocation().distance(block.getLocation()) > 10.0)
                        continue;

                    player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
                }
            }
        }
    }
}
