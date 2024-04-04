package net.slqmy.castle_siege_plugin.game;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.arena.CastleSiegeArena;
import net.slqmy.castle_siege_plugin.game.classes.*;
import net.slqmy.castle_siege_plugin.game.classes.base.GameClass;
import net.slqmy.castle_siege_plugin.game.data.CastleSiegeTeamBase;
import net.slqmy.castle_siege_plugin.game.data.TeamPlayer;
import net.slqmy.castle_siege_plugin.game.teams.CastleSiegeTeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public final class CastleSiegeGame {
    private final static Random random = new Random();

    private final CastleSiegePlugin plugin;
    private final CastleSiegeArena arena;
    private final List<Player> players;
    private final List<CastleSiegeTeam> teams;

    public CastleSiegeGame(CastleSiegeArena arena, List<Player> players) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.arena = arena;
        this.players = players;
        this.teams = new ArrayList<>();
    }

    public void loadMap() {

    }

    public void startGame() {
        createTeams();
        assignPlayersToTeams();
        spawnPlayers();
        assignClasses();
        preparePlayers();
        sendGuideMessages();
        sendClassMessages();
    }
    private void createTeams() {
        List<CastleSiegeTeamBase> teamBases = arena.getBases();

        for (CastleSiegeTeamBase teamBase : teamBases) {
            teams.add(new CastleSiegeTeam(teamBase));
        }
    }

    private void assignPlayersToTeams() {
        int totalTeams = teams.size();
        int i = 0;

        for (Player player : players) {
            CastleSiegeTeam team = teams.get(i % totalTeams);
            team.getTeamPlayers().add(new TeamPlayer(player, team));

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
