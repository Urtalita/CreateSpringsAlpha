package net.Portality.createsprings.blocks.advanced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.world.level.block.WeepingVinesPlantBlock.SHAPE;

public class ObsidianPlateBlock extends DirectionalBlock {
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

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
    public VoxelShape getShape(BlockState state, BlockGetter Getter, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPES.get(state.getValue(FACING));
    }
}
