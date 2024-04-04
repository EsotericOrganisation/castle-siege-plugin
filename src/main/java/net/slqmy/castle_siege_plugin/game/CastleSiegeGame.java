package net.slqmy.castle_siege_plugin.game;

import net.slqmy.castle_siege_plugin.game.arena.CastleSiegeArena;
import net.slqmy.castle_siege_plugin.game.data.TeamPlayer;
import net.slqmy.castle_siege_plugin.game.teams.CastleSiegeTeam;
import net.slqmy.castle_siege_plugin.game.data.CastleSiegeTeamBase;
import org.bukkit.entity.Player;

import java.util.*;

public class CastleSiegeGame {

    private final static Random random = new Random();

    private final CastleSiegeArena arena;
    private final List<Player> players;
    private final List<CastleSiegeTeam> teams;

    public CastleSiegeGame(CastleSiegeArena arena, List<Player> players) {
        this.arena = arena;
        this.players = players;
        this.teams = new ArrayList<>();
    }

    public void loadMap() {

    }

    public void startGame() {

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

    private void preparePlayers() {
        for (CastleSiegeTeam team : teams) {
            team.spawnPlayers();
        }
    }

    private void sendGuideMessages () {

    }

    private void assignClasses() {

    }

    private void sendClassMessages() {

    }

}
