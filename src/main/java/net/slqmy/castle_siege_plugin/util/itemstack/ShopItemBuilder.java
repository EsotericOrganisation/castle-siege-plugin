package net.slqmy.castle_siege_plugin.util.itemstack;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.slqmy.castle_siege_plugin.npc.shop.ShopCurrency;
import net.slqmy.castle_siege_plugin.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.slqmy.castle_siege_plugin.managers.PersistentDataManager.key;

public final class ShopItemBuilder extends ItemStackBuilder {
    private final UUID uuid;

    private Integer cost;
    private ShopCurrency currency;

    @Getter
    private static final Map<UUID, ShopItemBuilder> buildersById = new HashMap<>();

    public ShopItemBuilder(Material material) {
        super(material);
        this.uuid = UUID.randomUUID();

        buildersById.put(uuid, this);
    }

    public ShopItemBuilder setPrice(int cost, ShopCurrency currency) {
        this.cost = cost;
        this.currency = currency;
        return this;
    }

    @Override
    public ShopItemBuilder setName(String name) {
        super.setName(name);
        return this;
    }

    public ItemStack build(Player player) {
        ItemStackBuilder builder = ItemStackBuilder.from(this);

        if (builder.getNameColour() == null)
            builder.setNameColour(NamedTextColor.RED);

        boolean canAfford = player.getInventory().containsAtLeast(new ItemStack(currency.getMaterial()), cost);
        String priceText = "<gray>Cost: " + Util.toMMFormat(currency.getColour()) + cost + " " + currency.toText(cost);

        List<String> originalLore = getLore();
        builder
            .setLore()
            .addLoreLines(priceText)
            .addLoreLines("")
            .addLoreLines(originalLore.toArray(String[]::new))
            .addLoreLines(canAfford
                ? "<yellow>Click to purchase!"
                : "<red>You don't have enough " + currency.toText(cost) + "!");

        ItemStack item = builder.build();
        ItemMeta meta = item.getItemMeta();

        setCanAffordData(meta, canAfford);
        setPriceData(meta, cost, currency.getMaterial());

        if (uuid != null)
            setUUIDData(meta);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        plugin.getLogger().info(item.getItemFlags() + "");
        return item;
    }

    private void setUUIDData(ItemMeta meta) {
        pdcManager.setUUIDValue(meta, key("shopitems.uuid"), uuid);
    }

    private void setCanAffordData(ItemMeta meta, boolean canAfford) {
        pdcManager.setBooleanValue(meta, key("shopitems.can_purchase"), canAfford);
    }

    private void setPriceData(ItemMeta meta, int cost, Material material) {
        pdcManager.setIntValue(meta, key("shopitems.price"), cost);
        pdcManager.setStringValue(meta, key("shopitems.price_material"), material.name());
    }
}
