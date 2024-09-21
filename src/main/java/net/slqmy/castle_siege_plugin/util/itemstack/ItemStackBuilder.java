package net.slqmy.castle_siege_plugin.util.itemstack;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.managers.PersistentDataManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static net.slqmy.castle_siege_plugin.managers.PersistentDataManager.key;

//imported from one of my other projects
@Getter
@Setter
@Accessors(chain = true)
@SuppressWarnings(value = "unused")
public class ItemStackBuilder {
    protected static final MiniMessage MM = MiniMessage.miniMessage();

    protected final CastleSiegePlugin plugin;
    protected final PersistentDataManager pdcManager;

    protected Material material;
    protected int amount;

    protected String name;
    protected TextColor nameColour;
    protected List<String> lore;

    protected boolean enchanted;
    protected boolean unbreakable;

    protected String customStringData;
    protected Boolean customBooleanData;

    public ItemStackBuilder() {
        this.plugin = CastleSiegePlugin.getInstance();
        this.pdcManager = plugin.getPdcManager();

        this.material = Material.AIR;
        this.amount = 1;
        this.lore = new ArrayList<>();
    }

    public ItemStackBuilder(Material material) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.pdcManager = plugin.getPdcManager();

        this.material = material;
        this.amount = 1;
        this.lore = new ArrayList<>();
    }

    public static ItemStackBuilder from(ItemStackBuilder other) {
        return new ItemStackBuilder()
            .setMaterial(other.getMaterial())
            .setAmount(other.getAmount())
            .setName(other.getName())
            .setNameColour(other.getNameColour())
            .setLore(other.getLore().toArray(String[]::new))
            .setUnbreakable(other.isUnbreakable())
            .setEnchanted(other.isEnchanted())
            .setCustomStringData(other.getCustomStringData())
            .setCustomBooleanData(other.getCustomBooleanData());
    }

    public ItemStackBuilder addLoreLines(String... lore) {
        this.lore.addAll(List.of(lore));
        return this;
    }

    public ItemStackBuilder setLore(String... lore) {
        this.lore = new ArrayList<>(List.of(lore));
        return this;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            Component text = nonItalicDeserialize(name);
            meta.displayName(text);

            if (nameColour != null)
                meta.displayName(text.color(nameColour));
        }

        if (!lore.isEmpty())
            meta.lore(lore.stream().map(this::nonItalicDeserialize).toList());

        if (enchanted)
            addEnchantGlint(meta);

        if (unbreakable)
            meta.setUnbreakable(true);

        if (customStringData != null)
            addStringData(meta, customStringData);

        if (customBooleanData != null)
            addBooleanData(meta, customBooleanData);

        item.setItemMeta(meta);
        return item;
    }

    protected void addEnchantGlint(ItemMeta meta) {
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    protected void addStringData(ItemMeta meta, String data) {
        pdcManager.setStringValue(meta, key("items.data"), data);
    }

    protected void addBooleanData(ItemMeta meta, boolean data) {
        pdcManager.setBooleanValue(meta, key("items.data"), data);
    }

    protected Component nonItalicDeserialize(String message) {
        return MM.deserialize("<!i>" + message);
    }
}
