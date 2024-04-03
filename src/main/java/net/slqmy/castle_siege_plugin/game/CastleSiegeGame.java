package net.slqmy.castle_siege_plugin.game;

import net.slqmy.castle_siege_plugin.game.data.CastleSiegeArena;
import net.slqmy.castle_siege_plugin.game.data.CastleSiegeTeam;
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
        List<CastleSiegeTeamBase> teamBases = arena.getTeamBases();

        int teamCount = teamBases.size();
        int playerCount = players.size();

        int remainder = teamCount % playerCount;
        int quotient = (int) Math.floor((double) playerCount / (double) teamCount);

        for (CastleSiegeTeamBase teamBase : teamBases) {
            teams.add(new CastleSiegeTeam(teamBase));
        }

        for (int i = 0; i < teamCount; i++) {
            CastleSiegeTeam team = teams.get(i);

            List<Player> teamPlayers = team.getPlayers();

            teamPlayers.addAll(players.subList(i * quotient, (i + 1) * quotient));

            if (remainder != 0) {
                teamPlayers.add(players.get(teamCount - i - 1));

                remainder--;
            }

            team.spawnPlayers();
        }
    }
    private void createTeams() {

    }

    private void assignTeams() {

    }

    private void preparePlayers() {

    }

    private void sendGuideMessages () {

    }

    private void assignClasses() {

    }

    private void sendClassMessages() {

    }

}
