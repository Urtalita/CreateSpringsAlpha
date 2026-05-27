package com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine;

import com.Portality.createsprings.items.CSpringsArmorMaterials;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem.SLOT;

public class BrokenPSEItem extends BaseArmorItem {

    public BrokenPSEItem(Properties properties) {
        super(CSpringsArmorMaterials.BROKEN_PSE, Type.CHESTPLATE, properties, Create.asResource("copper_diving"));
    }

    @Nullable
    public static BrokenPSEItem getWornBy(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return null;
        }
        if (!(livingEntity.getItemBySlot(SLOT).getItem() instanceof BrokenPSEItem item)) {
            return null;
        }
        return item;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        return super.onDroppedByPlayer(item, player);
    }
}
