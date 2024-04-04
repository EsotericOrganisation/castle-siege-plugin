package net.slqmy.castle_siege_plugin.util;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//imported from one of my other projects
@Getter
public class ItemStackBuilder {
    public static final NamespacedKey KEY = new NamespacedKey(CastleSiegePlugin.getInstance(), "custom_item_type");
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private Material material;
    private int amount;
    private String name;
    private TextColor nameColor;
    private List<String> lore;
    private boolean enchanted;
    private boolean unbreakable;
    private String customStringData;
    private Boolean customBooleanData;

    public ItemStackBuilder() {
        this.material = Material.AIR;
        this.amount = 1;
        this.name = null;
        this.nameColor = null;
        this.lore = new ArrayList<>();
        this.enchanted = false;
        this.unbreakable = false;
        this.customStringData = null;
        this.customBooleanData = null;
    }

    public ItemStackBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemStackBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStackBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemStackBuilder setNameColor(TextColor nameColor) {
        this.nameColor = nameColor;
        return this;
    }

    public ItemStackBuilder addLoreLines(String... lore) {
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }

    public ItemStackBuilder setLore(String... lore) {
        this.lore = List.of(lore);
        return this;
    }

    public ItemStackBuilder setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;
        return this;
    }

    public ItemStackBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemStackBuilder setCustomData(String data) {
        this.customStringData = data;
        return this;
    }

    public ItemStackBuilder setCustomData(boolean data) {
        this.customBooleanData = data;
        return this;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            Component text = nonItalicDeserialize(name);
            meta.displayName(nameColor == null ? text : text.color(nameColor));
        }

        if (!lore.isEmpty()) {
            meta.lore(lore.stream().map(this::nonItalicDeserialize).toList());
        }

        if (enchanted) {
            addEnchantGlint(meta);
        }

        if (unbreakable) {
            meta.setUnbreakable(true);
        }

        if (customStringData != null) {
            addStringData(meta, customStringData);
        }

        if (customBooleanData != null) {
            addBooleanData(meta, customBooleanData);
        }

        item.setItemMeta(meta);
        return item;
    }

    private void addEnchantGlint(ItemMeta meta) {
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    private void addStringData(ItemMeta meta, String data) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY, PersistentDataType.STRING, data);
    }

    private void addBooleanData(ItemMeta meta, boolean data) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY, PersistentDataType.BOOLEAN, data);
    }

    private Component nonItalicDeserialize(String message) {
        return MM.deserialize("<!i>" + message);
    }
}
