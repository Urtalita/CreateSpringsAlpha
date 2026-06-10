package com.Portality.createsprings.blocks.advanced.SpringCatapult;

import com.Portality.createsprings.blocks.CSpringsBlockEntities;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class SpringCatapultBlock extends KineticBlock implements IBE<SpringCatapultBlockEntity> {
    public SpringCatapultBlock(Properties properties) {
        super(properties);
    }

    public static final BooleanProperty CEILING = BooleanProperty.create("ceiling");

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        withBlockEntityDo(pLevel, pPos, be -> be.onRemove());
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_.add(CEILING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(CEILING, ctx.getClickedFace() == Direction.DOWN);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<SpringCatapultBlockEntity> getBlockEntityClass() {
        return SpringCatapultBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpringCatapultBlockEntity> getBlockEntityType() {
        return CSpringsBlockEntities.SPRING_CATAPULT.get();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == Direction.Axis.Y;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stackInHand, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        AtomicReference<ItemInteractionResult> result = new AtomicReference<>(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        Optional<SpringCatapultBlockEntity> teProvider = getBlockEntityOptional(pLevel, pPos);

        if(teProvider.isPresent()){
            SpringCatapultBlockEntity b = teProvider.get();
            Direction hitFace = pHit.getDirection();
            boolean isCeiling = pState.getValue(CEILING);

            if ((hitFace == Direction.UP && !isCeiling) || (hitFace == Direction.DOWN && isCeiling)) {

                // В NeoForge 1.21.1 используем обновленную систему кап
                var itemHandler = b.returnHandler();

                if (itemHandler != null) {
                    if (stackInHand.isEmpty()) {
                        // Извлечение
                        ItemStack extracted = itemHandler.extractItem(0, 64, false);
                        if (!extracted.isEmpty()) {
                            pPlayer.setItemInHand(pHand, extracted);
                            AllSoundEvents.DEPOT_PLOP.playOnServer(pLevel, pPos);
                            return ItemInteractionResult.SUCCESS;
                        }
                    } else {
                        ItemStack remainder = itemHandler.insertItem(0, stackInHand, false);
                        if (remainder.getCount() != stackInHand.getCount()) {
                            pPlayer.setItemInHand(pHand, remainder);
                            AllSoundEvents.DEPOT_SLIDE.playOnServer(pLevel, pPos);
                            return ItemInteractionResult.SUCCESS;
                        }
                    }
                    return ItemInteractionResult.CONSUME;
                }
            }
        }
        return result.get();
    }


    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SpringCatapultBlockEntity myBE) {
            return Mth.floor(myBE.progress * 15); // Получаем значение от BlockEntity
        }
        return 0;
    }
}
