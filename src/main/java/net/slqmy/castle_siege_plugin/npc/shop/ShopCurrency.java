package net.slqmy.castle_siege_plugin.npc.shop;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

@Getter
public enum ShopCurrency {
    COAL(Material.COAL, NamedTextColor.DARK_GRAY),
    IRON(Material.IRON_INGOT, NamedTextColor.WHITE),
    EMERALD(Material.EMERALD, NamedTextColor.DARK_GREEN),
    GOLD(Material.GOLD_INGOT, NamedTextColor.GOLD);

    private final Material material;
    private final NamedTextColor colour;

    ShopCurrency(Material material, NamedTextColor colour) {
        this.material = material;
        this.colour = colour;
    }

    public String toText(int cost) {
        if (this == EMERALD) {
            return cost > 1 ? "Emeralds" : "Emerald";
        } else {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}
