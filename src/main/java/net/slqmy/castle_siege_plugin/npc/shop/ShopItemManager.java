package net.slqmy.castle_siege_plugin.npc.shop;

import net.slqmy.castle_siege_plugin.npc.shop.shops.BlacksmithShop;
import net.slqmy.castle_siege_plugin.util.itemstack.ShopItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ShopItemManager {
    private final ShopItemRegistry registry;

    public ShopItemManager() {
        this.registry = new ShopItemRegistry();

        registerItems();
    }

    public List<ItemStack> get(BlacksmithShop.Tab tab, Player player) {
        return registry.get(tab, player);
    }

    private void registerItems() {
        registry.registerItem(BlacksmithShop.Tab.WEAPONS,
            new ShopItemBuilder(Material.STONE_SWORD).setPrice(10, ShopCurrency.IRON).setName("Stone Sword"));
        registry.registerItem(BlacksmithShop.Tab.WEAPONS,
            new ShopItemBuilder(Material.IRON_SWORD).setPrice(10, ShopCurrency.EMERALD).setName("Iron Sword"));
        registry.registerItem(BlacksmithShop.Tab.WEAPONS,
            new ShopItemBuilder(Material.DIAMOND_SWORD).setPrice(4, ShopCurrency.GOLD).setName("Diamond Sword"));
        registry.registerItem(BlacksmithShop.Tab.WEAPONS,
            new ShopItemBuilder(Material.DIAMOND_AXE).setPrice(7, ShopCurrency.GOLD).setName("Diamond Sword"));
    }

    private static final class ShopItemRegistry {
        private final Map<BlacksmithShop.Tab, List<ShopItemBuilder>> blacksmithItems;

        public ShopItemRegistry() {
            this.blacksmithItems = new HashMap<>();
        }

        public void registerItem(BlacksmithShop.Tab tab, ShopItemBuilder builder) {
            blacksmithItems.computeIfAbsent(tab, k -> new ArrayList<>()).add(builder);
        }

        public List<ItemStack> get(BlacksmithShop.Tab tab, Player player) {
            if (!blacksmithItems.containsKey(tab)) return null;

            return blacksmithItems.get(tab).stream()
                .map(builder -> builder.build(player))
                .toList();
        }
    }
}
