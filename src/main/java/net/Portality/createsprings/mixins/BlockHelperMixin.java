package net.Portality.createsprings.mixins;

import com.simibubi.create.foundation.utility.BlockHelper;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.IConnectableToPSKI;
import net.createmod.catnip.nbt.NBTProcessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockHelper.class)
public class BlockHelperMixin {
    @Inject(
            method = "prepareBlockEntityData",
            at = @At("RETURN"),
            remap = false,
            cancellable = true
    )
    private static void prepareBlockEntityData(BlockState blockState, BlockEntity blockEntity, CallbackInfoReturnable<CompoundTag> cir) {
        if(blockEntity instanceof IConnectableToPSKI spring){
            spring.setStored(0);
            CompoundTag data = blockEntity.saveWithFullMetadata();
            cir.setReturnValue(NBTProcessors.process(blockState, blockEntity, data, true));
        }
    }
}
