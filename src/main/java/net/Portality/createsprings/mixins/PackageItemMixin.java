package net.Portality.createsprings.mixins;

import com.simibubi.create.content.logistics.box.PackageItem;
import net.Portality.createsprings.Items.advanced.hat.HatItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PackageItem.class)
public class PackageItemMixin {

    @Inject(
            method = "isPackage",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )

    private static void isPackage(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if(stack.getItem() instanceof HatItem){
            cir.setReturnValue(true);
        }
    }
}
