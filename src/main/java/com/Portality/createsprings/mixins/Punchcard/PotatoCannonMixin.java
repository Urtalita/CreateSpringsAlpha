package com.Portality.createsprings.mixins.Punchcard;

import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.advanced.Punchcard.ExecutorInfo;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardExecutor;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardInterpritator;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(PotatoCannonItem.class)
public class PotatoCannonMixin extends ItemDummyMixin{

    @Override
    public void onInventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected, CallbackInfo ci) {
        super.onInventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected, ci);
        if(pEntity instanceof Player player){
            if(pLevel.getGameTime() % 10 == 0){
                if(pIsSelected){
                    PunchcardInterpritator.DoPunchcardLogic(new ExecutorInfo(pStack, pLevel, player, pSlotId, true, PunchcardExecutor.POTATO_CANON, AllItems.POTATO_CANNON.get()));
                } else {
                    PunchcardInterpritator.DoPunchcardLogic(new ExecutorInfo(pStack, pLevel, player, pSlotId, false, PunchcardExecutor.POTATO_CANON, AllItems.POTATO_CANNON.get()));
                }
            }
        }
    }

    @Override
    public void overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess, CallbackInfoReturnable<Boolean> cir) {
        super.overrideOtherStackedOnMe(pStack, pOther, pSlot, pAction, pPlayer, pAccess, cir);
        Supplier<Item>[] allowedModifficators = new Supplier[]{
                () -> CSpringsItems.PUNCHCARD.get()
        };

        if (new SpringPoweredCore(0, allowedModifficators).overrideOtherStackedOnMe(pStack, pOther, pSlot, pAction, pPlayer, pAccess)) {
            cir.setReturnValue(true);
        }
        cir.setReturnValue(false);
    }

    @Override
    public void getTooltipImage(ItemStack pStack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        super.getTooltipImage(pStack, cir);
        cir.setReturnValue(SpringPoweredCore.getTooltipImage(pStack));
    }
}
