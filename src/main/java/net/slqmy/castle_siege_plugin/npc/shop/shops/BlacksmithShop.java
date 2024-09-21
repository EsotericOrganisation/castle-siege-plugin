package net.slqmy.castle_siege_plugin.npc.shop.shops;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.api.gui.BaseGUI;
import net.slqmy.castle_siege_plugin.npc.shop.ShopItemManager;
import net.slqmy.castle_siege_plugin.util.Util;
import net.slqmy.castle_siege_plugin.util.itemstack.ShopItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;

import java.util.List;
import java.util.UUID;

import static net.slqmy.castle_siege_plugin.managers.PersistentDataManager.key;

public final class BlacksmithShop extends BaseGUI {
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final ShopItemManager shopItemManager;

    private final List<Integer> tabSlots;
    private final List<Integer> purchasableSlots;

    private Tab currentTab;

    public BlacksmithShop() {
        super(MM.deserialize("Blacksmith"), 5);

        this.shopItemManager = new ShopItemManager();

        this.tabSlots = List.of(1, 2, 3, 5, 6, 7);
        this.purchasableSlots = List.of(19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

        this.currentTab = Tab.QUICK_BUY;
        this.frozen = true;

        initListeners();
        registerEvents();
    }

    @Override
    public void show(Player player) {
        this.player = player;
        refresh();

        super.show(player);
    }

    private void initListeners() {
        onOpen();
        onClose();

        onTabChange();
        onItemPurchase();
    }

    private void refresh() {
        gui.clear();

        setFillers();
        highlightCurrentTab();
        populateTabs();
        fillPurchasableItems();
    }

    private void setFillers() {
        setItems(new ItemStack(Material.GRAY_STAINED_GLASS_PANE),9, 10, 11, 12, 13, 14, 15, 16, 17);
    }

    private void highlightCurrentTab() {
        setItem(currentTab.getDisplaySlot(), new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
    }

    private void populateTabs() {
        setItem(1, new ItemStack(Material.BUNDLE));
        setItem(2, new ItemStack(Material.STONE_SWORD));
        setItem(3, new ItemStack(Material.CHAINMAIL_BOOTS));
        setItem(5, new ItemStack(Material.BREWING_STAND));
        setItem(6, new ItemStack(Material.COOKED_BEEF));
        setItem(7, new ItemStack(Material.TNT_MINECART));
    }

    private void onOpen() {
        this.onOpen = (event) -> Util.playSound(event.getPlayer(), org.bukkit.Sound.ENTITY_VILLAGER_TRADE);
    }

    private void onClose() {
        this.onClose = null;
    }

    private void onTabChange() {
        for (int slot : tabSlots) {
            setOnClick(slot, (event) -> {
                this.currentTab = Tab.fromIndex(tabSlots.indexOf(slot));

                Util.playSound(event.getWhoClicked(), org.bukkit.Sound.UI_BUTTON_CLICK);
                refresh();
            });
        }
    }

    private void onItemPurchase() {
        for (int slot : purchasableSlots) {
            setOnClick(slot, this::onClick);
        }
    }

    private void fillPurchasableItems() {
        List<ItemStack> items = shopItemManager.get(currentTab, player);
        if (items == null) return;

        List<ItemStack> subList = items.subList(0, Math.min(items.size(), purchasableSlots.size()));
        for (ItemStack item : subList) {
            setItem(purchasableSlots.get(items.indexOf(item)), item);
        }
    }

    private void onClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        Player player = (Player) event.getWhoClicked();

        PersistentDataHolder holder = clickedItem.getItemMeta();
        Boolean canPurchase = pdcManager.getBooleanValue(holder, key("shopitems.can_purchase"));

        if (canPurchase != null && canPurchase) {
            UUID itemId = pdcManager.getUUIDValue(holder, key("shopitems.uuid"));
            Integer price = pdcManager.getIntValue(holder, key("shopitems.price"));
            String material = pdcManager.getStringValue(holder, key("shopitems.price_material"));

            assert price != null;
            boolean removed = Util.removeItems(player.getInventory(), Material.valueOf(material), price);

            if (removed) {
                Util.playSound(player, org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING);
                refresh();

                Util.addToInvOrDrop(player, ShopItemBuilder.getBuildersById().get(itemId).build());
            } else {
                Util.playSound(player, org.bukkit.Sound.ENTITY_VILLAGER_NO);
            }
        } else {
            Util.playSound(player, org.bukkit.Sound.ENTITY_VILLAGER_NO);
        }
    }

    @Getter
    public enum Tab {
        QUICK_BUY(0),
        WEAPONS(1),
        ARMOUR(2),
        POTIONS(3),
        FOOD(4),
        UTILITY(5);

        private final int index;

        Tab(int index) {
            this.index = index;
        }

        public int getDisplaySlot() {
            return 10 + index + (index > 2 ? 1 : 0);
        }

        public static Tab fromIndex(int index) {
            return switch (index) {
                case 0 -> QUICK_BUY;
                case 1 -> WEAPONS;
                case 2 -> ARMOUR;
                case 3 -> POTIONS;
                case 4 -> FOOD;
                case 5 -> UTILITY;
                default -> null;
            };
        }
    }
}
