package net.Portality.createsprings.utill.burner;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlazeBurnerBlockEntity.class)
public class BlazeBurnerBlockEntityMixin implements CshatAccessor {
    @Unique
    private boolean cshat = false;
    @Unique
    private ItemStack cshatStack;

    @Override
    public boolean getCshat() {
        return this.cshat;
    }

    @Override
    public void setCshat(boolean value) {
        this.cshat = value;
    }

    @Override
    public ItemStack getCshatStack() {
        return this.cshatStack;
    }

    @Override
    public void setCshatStack(ItemStack value) {
        this.cshatStack = value;
    }

    @Inject(
            method = "read",
            at = @At("TAIL"),
            remap = false
    )
    private void read(CompoundTag compound, boolean clientPacket, CallbackInfo ci){
        cshat = compound.getBoolean("cshat");
        cshatStack = ItemStack.of(compound.getCompound("hatStack"));
    }

    @Inject(
            method = "write",
            at = @At("TAIL"),
            remap = false
    )
    private void write(CompoundTag compound, boolean clientPacket, CallbackInfo ci){
        compound.putBoolean("cshat", cshat);
        if (cshatStack != null) compound.put("hatStack", cshatStack.serializeNBT());
    }
}
