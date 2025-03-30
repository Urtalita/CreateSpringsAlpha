package net.Portality.createsprings.blocks.advanced.friction_welder;

import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        if (!player.mayBuild())
            return InteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return InteractionResult.FAIL;
        if (player.getItemInHand(handIn)
                .isEmpty()) {
            if (worldIn.isClientSide)
                return InteractionResult.SUCCESS;
            withBlockEntityDo(worldIn, pos, be -> {
                if(!be.isMainBlock)
                    return;
                if (be.running_) {
                    be.disassemble();
                    return;
                }
                be.SetAssemble();
            });

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
