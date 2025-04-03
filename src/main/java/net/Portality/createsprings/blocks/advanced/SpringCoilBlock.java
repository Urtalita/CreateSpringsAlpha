package net.Portality.createsprings.blocks.advanced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
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
        switch (facing) {
            case SOUTH -> {
                return Block.box(
                        16 - dirBoxEnd.x, dirBoxStart.y, 16 - dirBoxEnd.z,
                        16 - dirBoxStart.x, dirBoxEnd.y, 16 - dirBoxStart.z
                );
            }
            case EAST -> {
                return Block.box(
                        dirBoxStart.z, dirBoxStart.y, 16 - dirBoxEnd.x,
                        dirBoxEnd.z, dirBoxEnd.y, 16 - dirBoxStart.x
                );
            }
            case WEST -> {
                return Block.box(
                        16 - dirBoxEnd.z, dirBoxStart.y, dirBoxStart.x,
                        16 - dirBoxStart.z, dirBoxEnd.y, dirBoxEnd.x
                );
            }
            case UP ->
            {
                return Block.box(
                        dirBoxStart.x, dirBoxStart.z, dirBoxStart.y,
                        dirBoxEnd.x, dirBoxEnd.z, dirBoxEnd.y
                );
            }
            case DOWN ->
            {
                return Block.box(
                        dirBoxStart.x, 16 - dirBoxEnd.z, dirBoxStart.y,
                        dirBoxEnd.x, 16 - dirBoxStart.z, dirBoxEnd.y
                );
            }
            default -> {
                return Block.box(
                        dirBoxStart.x, dirBoxStart.y, dirBoxStart.z,
                        dirBoxEnd.x, dirBoxEnd.y, dirBoxEnd.z
                );
            }
        }
    }
}
