package net.Portality.createsprings.blocks.advanced.AndesiteMold;

import com.simibubi.create.foundation.block.IBE;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class AndesiteMoldBlock extends DirectionalBlock implements IBE<MoldBlockEntity> {
    public AndesiteMoldBlock(Properties p_52591_) {
        super(p_52591_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getNearestLookingDirection();
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return onBlockEntityUse(pLevel, pPos, b ->{
            if(b.heldStack.isEmpty()){return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);}
            if(pPlayer.getInventory().getFreeSlot() == -1){return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);}

            pPlayer.getInventory().add(ModBlocks.LARGE_SPRING_COIL.asStack());
            pLevel.setBlock(pPos,
                    ModBlocks.ANDESITE_MOLD.get().defaultBlockState().setValue(FACING, Direction.UP), 3);
            return InteractionResult.CONSUME;
        });
    }

    @Override
    public Class<MoldBlockEntity> getBlockEntityClass() {
        return MoldBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MoldBlockEntity> getBlockEntityType() {
        return ModBlockEntities.MOLD.get();
    }
}
