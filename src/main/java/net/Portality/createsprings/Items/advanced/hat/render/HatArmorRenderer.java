package net.Portality.createsprings.Items.advanced.hat.render;

import dev.engine_room.flywheel.lib.model.Models;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class HatArmorRenderer implements IClientItemExtensions {
    @Override
    public @NotNull Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        return (Model) Models.partial(CSpringsPartalModels.HAT);
    }
}
