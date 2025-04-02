package net.Portality.createsprings.blocks.advanced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class SpringCoilBlock extends DirectionalBlock {
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
    private static Vec3 dirBoxStart;
    private static Vec3 dirBoxEnd;

    public SpringCoilBlock(Properties properties, Vec3 dirBoxStart, Vec3 dirBoxEnd) {
        super(properties);
        this.dirBoxStart = dirBoxStart;
        this.dirBoxEnd = dirBoxEnd;
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
        Direction facing = state.getValue(FACING);
        return calculateVoxelShape(facing);
    }

    private VoxelShape calculateVoxelShape(Direction facing) {
        double min, max;
        switch (facing) {
            case EAST:
                min = 16 - dirBoxEnd.x * 16;
                max = 16 - dirBoxStart.x * 16;
                return Block.box(min, 0, 0, max, 16, 16);
            case WEST:
                min = dirBoxStart.x * 16;
                max = dirBoxEnd.x * 16;
                return Block.box(min, 0, 0, max, 16, 16);
            case UP:
                min = 16 - dirBoxEnd.y * 16;
                max = 16 - dirBoxStart.y * 16;
                return Block.box(0, min, 0, 16, max, 16);
            case DOWN:
                min = dirBoxStart.y * 16;
                max = dirBoxEnd.y * 16;
                return Block.box(0, min, 0, 16, max, 16);
            case SOUTH:
                min = 16 - dirBoxEnd.z * 16;
                max = 16 - dirBoxStart.z * 16;
                return Block.box(0, 0, min, 16, 16, max);
            case NORTH:
            default:
                min = dirBoxStart.z * 16;
                max = dirBoxEnd.z * 16;
                return Block.box(0, 0, min, 16, 16, max);
        }
    }
}
