package net.slqmy.castle_siege_plugin.managers;

import lombok.AccessLevel;
import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.util.pdc.UUIDTagType;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings(value = "unused")
public final class PersistentDataManager {
    private static final KeyManager keyManager = new KeyManager();

    public static NamespacedKey key(String path) {
        String[] parts = path.split("\\.");
        String group = parts[0];
        String member = parts[1];

        return keyManager.getNamespacedKeys().get(group).get(member);
    }

    public String getStringValue(PersistentDataHolder holder, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        return pdc.get(key, PersistentDataType.STRING);
    }

    public Boolean getBooleanValue(PersistentDataHolder holder, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        return pdc.get(key, PersistentDataType.BOOLEAN);
    }

    public Integer getIntValue(PersistentDataHolder holder, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        return pdc.get(key, PersistentDataType.INTEGER);
    }

    public int[] getIntListValue(PersistentDataHolder holder, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        return pdc.get(key, PersistentDataType.INTEGER_ARRAY);
    }

    public UUID getUUIDValue(PersistentDataHolder holder, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        return pdc.get(key, new UUIDTagType());
    }

    public void setStringValue(PersistentDataHolder holder, NamespacedKey key, String value) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.STRING, value);
    }

    public void setBooleanValue(PersistentDataHolder holder, NamespacedKey key, Boolean value) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.BOOLEAN, value);
    }

    public void setIntValue(PersistentDataHolder holder, NamespacedKey key, Integer value) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.INTEGER, value);
    }

    public void setIntListValue(PersistentDataHolder holder, NamespacedKey key, int[] value) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.INTEGER_ARRAY, value);
    }

    public void setUUIDValue(PersistentDataHolder holder, NamespacedKey key, UUID value) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, new UUIDTagType(), value);
    }

    @Getter(value = AccessLevel.PRIVATE)
    private static final class KeyManager {
        private final Map<String, Map<String, NamespacedKey>> namespacedKeys;

        public KeyManager() {
            this.namespacedKeys = new HashMap<>();

            for (Class<?> clazz : this.getClass().getDeclaredClasses()) {
                if (!clazz.isEnum()) continue;

                String name = clazz.getSimpleName().toLowerCase();
                namespacedKeys.put(name, new HashMap<>());

                for (Object enumMember : clazz.getEnumConstants()) {
                    NamespacedKeyWrapper keyWrapper = (NamespacedKeyWrapper) enumMember;
                    namespacedKeys.get(name).put(keyWrapper.getName(), keyWrapper.getKey());
                }
            }
        }

        private interface NamespacedKeyWrapper {
            String getName();
            NamespacedKey getKey();
        }

        public enum Items implements NamespacedKeyWrapper {
            CUSTOM_STRING_DATA("data");

            private final String name;
            private final NamespacedKey key;

            Items(String name) {
                this.name = name;
                this.key = new NamespacedKey(CastleSiegePlugin.getInstance(), name);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public NamespacedKey getKey() {
                return key;
            }
        }

        public enum ShopItems implements NamespacedKeyWrapper {
            UUID("uuid"),
            PRICE("price"),
            PRICE_MATERIAL("price_material"),
            CAN_PURCHASE("can_purchase");

            private final String name;
            private final NamespacedKey key;

            ShopItems(String name) {
                this.name = name;
                this.key = new NamespacedKey(CastleSiegePlugin.getInstance(), name);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public NamespacedKey getKey() {
                return key;
            }
        }

        public enum Items$Bows implements NamespacedKeyWrapper {
            ARROW_ORIGIN("arrow_origin");

            private final String name;
            private final NamespacedKey key;

            Items$Bows(String name) {
                this.name = name;
                this.key = new NamespacedKey(CastleSiegePlugin.getInstance(), name);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public NamespacedKey getKey() {
                return key;
            }
        }

        public enum Mobs implements NamespacedKeyWrapper {
            TYPE("type");

            private final String name;
            private final NamespacedKey key;

            Mobs(String name) {
                this.name = name;
                this.key = new NamespacedKey(CastleSiegePlugin.getInstance(), name);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public NamespacedKey getKey() {
                return key;
            }
        }
    }
}
