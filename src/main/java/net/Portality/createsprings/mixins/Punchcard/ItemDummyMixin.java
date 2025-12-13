package net.Portality.createsprings.mixins.Punchcard;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public class ItemDummyMixin {
    @Inject(
            method = "inventoryTick",
            at = @At("HEAD")
    )

    public void onInventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected, CallbackInfo ci){

    }

    @Inject(
            method = "overrideOtherStackedOnMe",
            at = @At("HEAD"),
            cancellable = true
    )

    public void overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess, CallbackInfoReturnable<Boolean> cir){
        // no logic
    }

    @Inject(
            method = "getTooltipImage",
            at = @At("HEAD"),
            cancellable = true
    )

    public void getTooltipImage(ItemStack pStack, CallbackInfoReturnable<Optional<TooltipComponent>> cir){
        // no logic
    }
}
