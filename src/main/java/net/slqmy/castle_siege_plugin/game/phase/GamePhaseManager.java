package net.slqmy.castle_siege_plugin.game.phase;

import lombok.Getter;
import lombok.Setter;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.game.data.arena.ArenaConfig;
import net.slqmy.castle_siege_plugin.util.interfaces.ITickable;

public final class GamePhaseManager implements ITickable {
    private final Game game;
    private final ArenaConfig arenaConfig;

    private final int attacksUntilDeathMatch;

    @Getter @Setter
    private GamePhase phase;
    @Getter @Setter
    private GamePhase nextPhase;
    @Getter @Setter
    private long duration;

    public GamePhaseManager(Game game) {
        this.game = game;
        this.arenaConfig = game.getArenaConfig();
        this.attacksUntilDeathMatch = arenaConfig.deathMatch().attackingSessionsPerTeamUntilDeathMatch() * 2;

        this.setPhase(GamePhase.LOBBY);
        this.setDurationM(Long.MAX_VALUE);
    }

    @Override
    public void tick() {
        if (duration-- <= 0)
            next();
    }

    public String getFormattedTime() {
        long seconds = duration / 20;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setDurationM(double minutes) {
        setDuration((long) (minutes * 60 * 20));
    }

    public void next() {
        switch (phase) {
            case LOBBY -> {
                this.setPhase(GamePhase.DEVELOPMENT);
                this.setNextPhase(GamePhase.ATTACKING);
                this.setDurationM(arenaConfig.developmentPeriodM());
            }
            case DEVELOPMENT -> {
                this.setPhase(GamePhase.ATTACKING);
                this.setNextPhase(GamePhase.REST);
                this.setDurationM(arenaConfig.attackingPeriodM());
            }
            case REST -> {
                if (game.getData().getTotalAttacks() >= attacksUntilDeathMatch) {
                    this.setPhase(GamePhase.DEATHMATCH);
                    this.setNextPhase(GamePhase.END);
                    this.setDurationM(arenaConfig.deathMatch().durationM());
                } else {
                    this.setPhase(GamePhase.ATTACKING);
                    this.setNextPhase(GamePhase.REST);
                    this.setDurationM(arenaConfig.attackingPeriodM());
                }
            }
            case ATTACKING -> {
                this.setPhase(GamePhase.REST);
                this.setDurationM(arenaConfig.restPeriodM());

                if (game.getData().getTotalAttacks() >= attacksUntilDeathMatch) {
                    this.setNextPhase(GamePhase.DEATHMATCH);
                } else {
                    this.setNextPhase(GamePhase.ATTACKING);
                }
            }
            case DEATHMATCH -> {
                this.setPhase(GamePhase.END);
                this.setNextPhase(GamePhase.NULL);
                this.setDurationM(0);
            }
            default -> this.setPhase(GamePhase.LOBBY);
        }
    }
}
