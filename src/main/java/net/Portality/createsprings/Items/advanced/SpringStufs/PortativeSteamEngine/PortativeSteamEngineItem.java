package net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.foundation.item.LayeredArmorItem;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.CspringsArmorMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;


public class PortativeSteamEngineItem extends ArmorItem {
    public static final EquipmentSlot SLOT = EquipmentSlot.CHEST;

    public PortativeSteamEngineItem(Properties properties) {
        super(CspringsArmorMaterials.HAT, Type.CHESTPLATE, properties);
    }

    @Nullable
    public static PortativeSteamEngineItem getWornBy(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return null;
        }
        if (!(livingEntity.getItemBySlot(SLOT).getItem() instanceof PortativeSteamEngineItem item)) {
            return null;
        }
        return item;
    }
}
