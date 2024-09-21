package net.slqmy.castle_siege_plugin.game.data.team;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.game.data.player.TeamPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public final class Team {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final Random random = new Random();

    private final String id;

    private final CastleSiegePlugin plugin;
    private final Game game;

    private final TeamBaseData baseData;
    private final List<TeamPlayer> teamPlayers;
    private final List<LivingEntity> teamEntities;
    private final String name;

    private final List<TeamPlayer> attackingPlayers;

    @Setter
    private org.bukkit.scoreboard.Team scoreboardTeam;
    private BukkitTask tickManager;

    public Team(TeamBaseData baseData, Game game) {
        this.id = baseData.id();

        this.plugin = CastleSiegePlugin.getInstance();
        this.game = game;

        this.baseData = baseData;
        this.teamPlayers = new ArrayList<>();
        this.teamEntities = new ArrayList<>();
        this.name = baseData.name();

        this.attackingPlayers = new ArrayList<>();

        startTasks();
    }

    public void unregister() {
        tickManager.cancel();
        scoreboardTeam.unregister();

        teamPlayers.forEach(TeamPlayer::unregister);
    }

    public void addPlayer(Player player) {
        teamPlayers.add(new TeamPlayer(player, this));
        teamEntities.add(player);
    }

    public void removePlayer(Player player) {
        teamPlayers.removeIf(teamPlayer -> teamPlayer.getPlayer().equals(player));
        teamEntities.remove(player);
    }

    public TeamPlayer getKing() {
        return teamPlayers.stream()
            .filter(TeamPlayer::isKing)
            .findFirst()
            .orElse(null);
    }

    public void spawnPlayers() {
        teamPlayers.forEach(TeamPlayer::spawn);
    }

    public void equipPlayers() {
        teamPlayers.forEach(TeamPlayer::equipPlayer);
    }

    public void sendClassMessages() {
        teamPlayers.forEach(TeamPlayer::sendClassInfo);
    }

    public void createNameAndHealthTags() {
        teamPlayers.forEach(TeamPlayer::createHealthDisplay);
        teamPlayers.forEach(TeamPlayer::createNameTag);
    }

    public void showScoreboardsAndRelatedInfo() {
        teamPlayers.forEach(TeamPlayer::joinScoreboardTeam);
        teamPlayers.forEach(TeamPlayer::showScoreboard);
    }

    public void tickPlayersAsync() {
        teamPlayers.forEach(TeamPlayer::tickAsyncTasks);
    }

    private void startTasks() {
        List<Block> windowBlocks = game.getMapData().getWindowBlocks().get(id);
        this.tickManager = new OneWayBlockHandler(windowBlocks).runTaskTimer(plugin, 0L, 1L);
    }

    private final class OneWayBlockHandler extends BukkitRunnable {
        private final List<? extends Block> windowBlocks;

        public OneWayBlockHandler(List<? extends Block> windowBlocks) {
            this.windowBlocks = windowBlocks;
        }

        @Override
        public void run() {
            for (Block block : windowBlocks) {
                for (TeamPlayer teamPlayer : Team.this.teamPlayers) {
                    Player player = teamPlayer.getPlayer();

                    if (player.getLocation().distance(block.getLocation()) > 10.0)
                        continue;

                    player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
                }
            }
        }
    }
}
