package com.Portality.createsprings.blocks.advanced.kinetic_interface;

import com.Portality.createsprings.blocks.advanced.ModBlockEntities;
import com.Portality.createsprings.utill.Helpers.HitboxHelper;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KineticInterfaceBlock extends DirectionalKineticBlock implements IBE<KineticInterfaceBlockEntity> {
    public KineticInterfaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if(context.getPlayer().isShiftKeyDown()){
            return defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
        }
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(state.getValue(FACING).getAxis() == Direction.Axis.Z){
            return HitboxHelper.calculateDierectionalVoxelShape(state.getValue(FACING),
                    new Vec3(0, 0, 3),
                    new Vec3(16, 16, 16));
        }
        return HitboxHelper.calculateDierectionalVoxelShape(state.getValue(FACING),
                new Vec3(0, 0, 0),
                new Vec3(16, 16, 13));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_,
                                boolean p_220069_6_) {
        boolean hasSignal = world.hasNeighborSignal(pos);
        withBlockEntityDo(world, pos, KineticInterfaceBlockEntity::neighbourChanged);
        withBlockEntityDo(world, pos, be -> be.setGenerating(hasSignal));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return getBlockEntityOptional(worldIn, pos).map(be -> be.isConnected() ? 15 : 0)
                .orElse(0);
    }

    @Override
    public Class<KineticInterfaceBlockEntity> getBlockEntityClass() {
        return KineticInterfaceBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticInterfaceBlockEntity> getBlockEntityType() {
        return ModBlockEntities.KINETIC_INTERFACE.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction facing = state.getValue(FACING);
        return face == facing || face == facing.getOpposite();
    }
}
