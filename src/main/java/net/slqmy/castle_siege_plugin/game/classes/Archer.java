package net.slqmy.castle_siege_plugin.game.classes;

import net.slqmy.castle_siege_plugin.game.classes.base.CustomisableGameClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class Archer extends CustomisableGameClass {
    private static final String NAME = "Archer";
    public Archer() {
        super(NAME);
    }

    @Override
    public void equipPlayer(Player player) {
        Inventory inventory = player.getInventory();
        inventory.clear();
    }
}
