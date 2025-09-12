package net.Portality.createsprings.mixins;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.fluid.CSpringsFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.simibubi.create.content.fluids.spout.SpoutBlockEntity.FILLING_TIME;
import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

@Mixin(SpoutBlockEntity.class)
public abstract class MixinSpoutBe {

    @Shadow(remap = false)
    private int processingTicks;

    @Shadow protected abstract FluidStack getCurrentFluidInTank();

    @Shadow public BlockSpoutingBehaviour customProcess;

    @Shadow private SmartFluidTankBehaviour tank;

    private int delay = 20;
    private boolean filled = false;

    @Inject(
            method = "tick()V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false // Отключаем ремаппинг если используется ForgeGradle 5+
    )

    private void tick(CallbackInfo ci) {
        BlockEntity be = (SpoutBlockEntity) (Object) this;
        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();

        if (filled){
            delay--;
            if(delay == -1){
                level.setBlock(pos.below(2),
                        ModBlocks.FILLED_ANDESITE_MOLD.get().defaultBlockState().setValue(FACING, Direction.UP), 3);
                filled = false;
                delay = 20;
            }
        } else {
            FluidStack currentFluidInTank = getCurrentFluidInTank();
            if (processingTicks == -1 && (((SpoutBlockEntity) be).isVirtual() || !level.isClientSide())
                    && currentFluidInTank.getFluid().getFluidType() == CSpringsFluids.SPRING_ALLOY.getType()
                    && currentFluidInTank.getAmount() >= 500) {

                BlockPos filling = pos.below(2);
                BlockState state = level.getBlockState(filling);
                if (state.getBlock() == ModBlocks.ANDESITE_MOLD.get()) {
                    processingTicks = FILLING_TIME;

                    tank.getPrimaryHandler()
                            .setFluid(FluidHelper.copyStackWithAmount(currentFluidInTank,
                                    currentFluidInTank.getAmount() - 500));
                    filled = true;
                    AllSoundEvents.SPOUTING.playOnServer(level, pos);

                    ((SpoutBlockEntity) be).notifyUpdate();
                }
            }
        }
    }
}