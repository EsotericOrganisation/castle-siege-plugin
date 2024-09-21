package net.slqmy.castle_siege_plugin.game.data.player;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.slqmy.castle_siege_plugin.api.scoreboard.FastBoard;
import net.slqmy.castle_siege_plugin.api.scoreboard.ScoreboardField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class PlayerScoreboard {
    private final FastBoard scoreboard;
    private final Map<ScoreboardField, Integer> fieldToLineIndex;

    private final List<Component> originalLines;
    private final List<Component> lines;

    public PlayerScoreboard(FastBoard scoreboard) {
        this.scoreboard = scoreboard;
        this.fieldToLineIndex = new HashMap<>();

        this.originalLines = scoreboard.getLines();
        this.lines = scoreboard.getLines();

        for (ScoreboardField field : ScoreboardField.values()) {
            fieldToLineIndex.put(field, field.getLineIndex());
        }
    }

    public PlayerScoreboard updateField(ScoreboardField field, Component suffix, Character replace) {
        Component updatedLine = originalLines
            .get(fieldToLineIndex.get(field))
            .replaceText(config -> config.match(replace.toString()).replacement(suffix));

        return replaceField(field, updatedLine);
    }

    public PlayerScoreboard replaceField(ScoreboardField field, Component text) {
        lines.set(fieldToLineIndex.get(field), text);
        return this;
    }

    public void sendUpdatePackets() {
        scoreboard.updateLines(lines);
    }
}
