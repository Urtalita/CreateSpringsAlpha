package com.Portality.createsprings.mixins;

import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.advanced.hat.HatItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GogglesItem.class)
public class GogglesMixin {
    @Inject(
            method = "isWearingGoggles",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )

    private static void tick(Player player, CallbackInfoReturnable<Boolean> cir) {
        if(player.getItemBySlot(EquipmentSlot.HEAD).getItem() == CSpringsItems.HAT.get()){
            cir.setReturnValue(HatItem.hasGoggles(player.getItemBySlot(EquipmentSlot.HEAD)));
        }
    }
}
