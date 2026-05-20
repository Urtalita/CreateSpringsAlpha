package com.Portality.createsprings.blocks.advanced.AndesiteMold;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.advanced.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import java.util.concurrent.atomic.AtomicReference;

public class AndesiteMoldBlock extends DirectionalBlock implements IBE<MoldBlockEntity> {
    public AndesiteMoldBlock(Properties p_52591_) {
        super(p_52591_);
    }
    public static final MapCodec<AndesiteMoldBlock> CODEC = simpleCodec(AndesiteMoldBlock::new);

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        AtomicReference<ItemInteractionResult> result = new AtomicReference<>(ItemInteractionResult.CONSUME);
        withBlockEntityDo(level, pos, b ->{
            if(b.heldStack.isEmpty()){
                result.set(super.useItemOn(stack, state, level, pos, player, hand, hitResult));}
            if(player.getInventory().getFreeSlot() == -1){result.set(super.useItemOn(stack, state, level, pos, player, hand, hitResult));}

            player.getInventory().add(CSpringsBlocks.LARGE_SPRING_COIL.asStack());
            level.setBlock(pos,
                    CSpringsBlocks.ANDESITE_MOLD.get().defaultBlockState().setValue(FACING, Direction.UP), 3);
        });
        return result.get();
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
