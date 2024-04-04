package net.slqmy.castle_siege_plugin.game.classes;

import net.slqmy.castle_siege_plugin.game.classes.base.GameClass;
import org.bukkit.entity.Player;

public final class King extends GameClass {

    public King() {
        super();
    }

    @Override
    public int getPlayerCount(int totalPlayerCount) {
        return 1;
    }

    @Override
    public void equipPlayer(Player player) {

    }
}
