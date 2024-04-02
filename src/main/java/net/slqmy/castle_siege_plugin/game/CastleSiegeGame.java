package net.slqmy.castle_siege_plugin.game;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class CastleSiegeGame {

    private final static Random random = new Random();

    private CastleSiegeArena arena;

    private List<Player> players;

    private final List<CastleSiegeTeam> teams = new ArrayList<>();

    public void startGame() {
        List<CastleSiegeTeamBase> teamBases = arena.getTeamBases();

        int teamCount = teamBases.size();
        int playerCount = players.size();

        int remainder = teamCount % playerCount;
        int quotient = (int) Math.floor(((double) playerCount) / ((double) teamCount));

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
}
