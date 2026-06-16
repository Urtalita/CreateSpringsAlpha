package com.Portality.createsprings.blocks.simpleCustomBlocks;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlock;
import com.google.common.base.Predicates;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.placement.PoleHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

public class ObsidianPlateBlock extends DirectionalBlock implements IWrenchable, SimpleWaterloggedBlock {
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final MapCodec<ObsidianPlateBlock> CODEC = simpleCodec(ObsidianPlateBlock::new);


    static {
        SHAPES.put(Direction.NORTH, Block.box(0, 0, 0, 16, 16, 1));
        SHAPES.put(Direction.SOUTH, Block.box(0, 0, 15, 16, 16, 16));
        SHAPES.put(Direction.EAST, Block.box(15, 0, 0, 16, 16, 16));
        SHAPES.put(Direction.WEST, Block.box(0, 0, 0, 1, 16, 16));
        SHAPES.put(Direction.UP, Block.box(0, 15, 0, 16, 16, 16));
        SHAPES.put(Direction.DOWN, Block.box(0, 0, 0, 16, 1, 16));
    }

    public ObsidianPlateBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getNearestLookingDirection();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        boolean isWater = fluidState.getType() == Fluids.WATER;
        return defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, isWater);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter Getter, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    @Deprecated
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    @Deprecated
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
