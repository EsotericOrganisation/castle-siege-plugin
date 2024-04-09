package net.slqmy.castle_siege_plugin.game;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.classes.*;
import net.slqmy.castle_siege_plugin.game.classes.base.GameClass;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeArenaConfig;
import net.slqmy.castle_siege_plugin.game.data.player.TeamPlayer;
import net.slqmy.castle_siege_plugin.game.data.team.CastleSiegeTeam;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeTeamBaseData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public final class CastleSiegeGame {
    @Getter
    private final static List<Player> allPlayers = new ArrayList<>();
    private final static Random random = new Random();

    private final CastleSiegePlugin plugin;
    private final CastleSiegeArenaConfig arenaConfig;
    private final List<Player> players;
    private final List<CastleSiegeTeam> teams;

    @Getter
    private CastleSiegeMap map;

    @Getter
    private final World world;

    public CastleSiegeGame(CastleSiegeArenaConfig arenaConfig, List<Player> players) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.arenaConfig = arenaConfig;
        this.players = players;
        this.teams = new ArrayList<>();
        this.world = Bukkit.getWorld(arenaConfig.getWorldName());

        allPlayers.addAll(players);
    }

    public void startGame() {
        loadMap();
        createTeams();
        assignPlayersToTeams();
        spawnPlayers();
        assignClasses();
        preparePlayers();
        sendGuideMessages();
        sendClassMessages();
    }

    private void loadMap() {
        map = new CastleSiegeMap(arenaConfig, this).loadConfig().loadMap();
    }
    private void createTeams() {
        List<CastleSiegeTeamBaseData> teamBases = arenaConfig.getBases();

        for (CastleSiegeTeamBaseData teamBase : teamBases) {
            teams.add(new CastleSiegeTeam(teamBase, this));
        }
    }

    private void assignPlayersToTeams() {
        int totalTeams = teams.size();
        int i = 0;

        for (Player player : players) {
            CastleSiegeTeam team = teams.get(i % totalTeams);
            team.addPlayer(player);

            i++;
        }
    }

    private void spawnPlayers() {
        loopThroughTeams(CastleSiegeTeam::spawnPlayers);
    }

    private void assignClasses() {
        List<GameClass> classes = List.of(
            new King(),
            new Berserk(),
            new Archer(),
            new Knight(),
            new Mage()
        );

        loopThroughTeams(team -> {
            List<TeamPlayer> teamPlayers = team.getTeamPlayers();
            List<TeamPlayer> remainingPlayers = new ArrayList<>(teamPlayers);

            classes.forEach(gameClass -> {
                int totalTeamPlayers = teamPlayers.size();
                int playerCount = gameClass.getPlayerCount(totalTeamPlayers);

                for (int i = 0; i < playerCount; i++) {
                    TeamPlayer teamPlayer = remainingPlayers.get(random.nextInt(remainingPlayers.size()));

                    plugin.getLogger().info("Assigning " + teamPlayer.getPlayer().getName() + " to " + gameClass);
                    teamPlayer.setGameClass(gameClass);
                    remainingPlayers.remove(teamPlayer);
                }
            });
        });
    }

    private void preparePlayers() {
        loopThroughTeams(team -> loopThroughPlayers(team, TeamPlayer::equipPlayer));
    }

    private void sendGuideMessages () {

    }

    private void sendClassMessages() {

    }

    private void loopThroughTeams(Consumer<CastleSiegeTeam> consumer) {
        for (CastleSiegeTeam team : teams) {
            consumer.accept(team);
        }
    }

    private void loopThroughPlayers(CastleSiegeTeam team, Consumer<TeamPlayer> consumer) {
        for (TeamPlayer teamPlayer : team.getTeamPlayers()) {
            consumer.accept(teamPlayer);
        }
    }

}
