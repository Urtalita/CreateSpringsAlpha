package net.Portality.createsprings.mixins.burner;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.utill.mixins.CshatAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlazeBurnerBlock.class)
public class BlazeBurnerBlockMixin {
    @Inject(
            method = "use",
            at = @At("TAIL"),
            remap = false
    )
    private void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack heldItem = player.getItemInHand(hand);
        IBE<BlazeBurnerBlockEntity> ibe = (IBE<BlazeBurnerBlockEntity>) (Object) this;

        if (heldItem.getItem() == ModItems.HAT.get())
             ibe.onBlockEntityUse(world, pos, bbte -> {
                 if(bbte instanceof CshatAccessor){
                     if(((CshatAccessor) bbte).getCshat()){
                        return null;
                     }

                     if(!heldItem.getOrCreateTag().contains("red"))putColor(heldItem);
                     heldItem.getOrCreateTag().putBoolean("prevent_place", true);

                     ((CshatAccessor) bbte).setCshatStack(heldItem.copy());
                     ((CshatAccessor) bbte).setCshat(true);
                     bbte.notifyUpdate();
                 }
                return null;
            });
    }

    private void putColor(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("red", 255);
        tag.putInt("blue", 255);
        tag.putInt("green", 255);

        tag.putInt("red1", 255);
        tag.putInt("blue1", 255);
        tag.putInt("green1", 255);
    }
}
