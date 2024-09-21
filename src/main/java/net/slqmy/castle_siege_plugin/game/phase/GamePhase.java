package net.slqmy.castle_siege_plugin.game.phase;

import lombok.Getter;

@Getter
public enum GamePhase {
    LOBBY("<yellow>Waiting ..."),
    DEVELOPMENT("<green>Grace Period"),
    ATTACKING("Attack"),
    REST("<green>Rest"),
    DEATHMATCH("<dark_red>Deathmatch"),
    END("<dark_gray>Game End"),
    NULL("<gray>None");

    private final String name;

    GamePhase(String name) {
        this.name = name;
    }
}
