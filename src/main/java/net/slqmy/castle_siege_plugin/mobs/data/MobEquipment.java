package net.slqmy.castle_siege_plugin.mobs.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Getter @Setter @Accessors(chain = true)
public final class MobEquipment {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    private ItemStack mainHand;
    private ItemStack offHand;

    public void equipMob(LivingEntity entity) {
        if (helmet != null) entity.setItemSlot(EquipmentSlot.HEAD, helmet);
        if (chestplate != null) entity.setItemSlot(EquipmentSlot.CHEST, chestplate);
        if (leggings != null) entity.setItemSlot(EquipmentSlot.LEGS, leggings);
        if (boots != null) entity.setItemSlot(EquipmentSlot.FEET, boots);

        if (mainHand != null) entity.setItemSlot(EquipmentSlot.MAINHAND, mainHand);
        if (offHand != null) entity.setItemSlot(EquipmentSlot.OFFHAND, offHand);
    }
}
