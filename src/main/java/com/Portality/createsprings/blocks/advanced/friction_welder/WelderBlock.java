package com.Portality.createsprings.blocks.advanced.friction_welder;

import com.Portality.createsprings.blocks.advanced.ModBlockEntities;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WelderBlock extends BearingBlock implements IBE<WelderBlockEntity> {
    public WelderBlock(Properties properties) {
        super(properties);
    }
    @Override
    public Class<WelderBlockEntity> getBlockEntityClass() {
        return WelderBlockEntity.class;
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return super.getMinimumRequiredSpeedLevel();
    }

    @Override
    public BlockEntityType<? extends WelderBlockEntity> getBlockEntityType() { return ModBlockEntities.FRICTION_WELDER.get(); }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild())
            return ItemInteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return ItemInteractionResult.FAIL;
        if (player.getItemInHand(hand)
                .isEmpty()) {
            if (level.isClientSide)
                return ItemInteractionResult.SUCCESS;
            withBlockEntityDo(level, pos, be -> {
                if (be.getRunning()) {
                    be.disassemble();
                    be.stopped = true;
                    return;
                }
                be.running = true;
            });

            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }



    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return super.getRotationAxis(state);
    }
}
