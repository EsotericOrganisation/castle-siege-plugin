package net.slqmy.castle_siege_plugin.api.scoreboard;

import lombok.Getter;

@Getter
public enum ScoreboardField {
    BORDER_START("border_start", 0),
    DATE("date", 1),
    NEXT_PHASE("next_phase", 2),
    PHASE("phase", 3),
    PHASE_TIME("phase_time", 4),

    TEAM("team", 6),
    KILLS("kills", 7),
    DEATHS("deaths", 8),
    LOCATION("location", 9),

    DISCORD("discord", 11),
    BORDER_END("border_end", 12);

    private final String identifier;
    private final int lineIndex;

    ScoreboardField(String identifier, int lineIndex) {
        this.identifier = identifier;
        this.lineIndex = lineIndex;
    }
}
