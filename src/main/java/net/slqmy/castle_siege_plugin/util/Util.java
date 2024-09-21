package net.slqmy.castle_siege_plugin.util;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class Util {
    private static final CastleSiegePlugin plugin = CastleSiegePlugin.getInstance();
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private Util() {}

    public static String toMMFormat(NamedTextColor colour) {
        return "<" + colour.toString() + ">";
    }

    public static void addToInvOrDrop(Player player, ItemStack item) {
        player.getInventory().addItem(item)
            .forEach((slot, leftover) -> player.getWorld().dropItem(player.getLocation(), item));
    }

    public static Component wrapInSquareBrackets(String text, String colour) {
        String colourFormat = "<" + colour + ">";
        String resetFormat = "</" + colour + ">";

        return MM.deserialize(colourFormat + "[" + resetFormat + text + colourFormat + "]" + resetFormat);
    }

    public static boolean removeItems(Inventory inventory, Material material, int amount) {
        return removeItems(inventory, new ItemStack(material), amount);
    }

    public static boolean removeItems(Inventory inventory, ItemStack item, int amount) {
        ItemStack[] contents = inventory.getContents();
        Map<ItemStack, Integer> itemsToBeRemoved = new HashMap<>();

        for (ItemStack itemStack : contents) {
            if (itemStack == null) continue;
            if (amount == 0) break;

            if (itemStack.isSimilar(item)) {
                int toBeRemoved = Math.min(amount, itemStack.getAmount());
                itemsToBeRemoved.put(itemStack, toBeRemoved);

                amount -= toBeRemoved;
            }
        }

        if (amount == 0) {
            itemsToBeRemoved.forEach((itemStack, removedAmount) -> itemStack.setAmount(itemStack.getAmount() - removedAmount));
        }

        return amount == 0;
    }

    public static void playSound(HumanEntity player, org.bukkit.Sound sound) {
        player.playSound(Sound.sound(sound.key(), Sound.Source.AMBIENT, 1.0f, 1.0f));
    }

    public static void playSound(HumanEntity player, org.bukkit.Sound sound, float volume, float pitch) {
        player.playSound(Sound.sound(sound.key(), Sound.Source.AMBIENT, volume, pitch));
    }
}
