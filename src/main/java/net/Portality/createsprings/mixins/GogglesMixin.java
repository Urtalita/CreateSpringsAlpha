package net.Portality.createsprings.mixins;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.Portality.createsprings.Items.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GogglesItem.class)
public class GogglesMixin {
    @Inject(
            method = "isWearingGoggles",
            at = @At("HEAD"),
            cancellable = true,
            remap = false // Отключаем ремаппинг если используется ForgeGradle 5+
    )

    private static void tick(Player player, CallbackInfoReturnable<Boolean> cir) {
        if(player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.HAT.get()){
            cir.setReturnValue(player.getItemBySlot(EquipmentSlot.HEAD).getOrCreateTag().getBoolean("goggles"));
        }
    }
}
