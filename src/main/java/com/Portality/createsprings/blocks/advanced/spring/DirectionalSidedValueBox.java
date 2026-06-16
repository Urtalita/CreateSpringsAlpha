package com.Portality.createsprings.blocks.advanced.spring;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class DirectionalSidedValueBox extends ValueBoxTransform.Sided {
    private Function<BlockState ,Direction> getDirection;
    private Vec2 positionOnSide;

    public DirectionalSidedValueBox(Function<BlockState, Direction> getDirection, float x, float y){
        this.getDirection = getDirection;
        this.positionOnSide = new Vec2(x, y);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace(positionOnSide.x, positionOnSide.y, 15.5f);
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Vec3 location = getSouthLocation();

        location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(getSide()), Direction.Axis.Y);
        location = VecHelper.rotateCentered(location, AngleHelper.verticalAngle(getSide()), Direction.Axis.X);

        Direction interfaceDirection = getDirection.apply(state).getOpposite();
        Direction.Axis sideAxis = getSide().getAxis();
        float angle = 180;
        if(interfaceDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE) angle += 180;
        if(interfaceDirection.getAxis() == Direction.Axis.X) angle -= 90;
        if(interfaceDirection.getAxis() == Direction.Axis.Z) angle += 90;

        if(interfaceDirection.getAxis() == Direction.Axis.Z && getSide() == Direction.UP) angle += 90;
        if(interfaceDirection.getAxis() == Direction.Axis.Z && getSide() == Direction.DOWN) angle -= 90;

        location = VecHelper.rotateCentered(location, angle, sideAxis);

        return location;
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        return getDirection.apply(state).getAxis() != direction.getAxis();
    }

    @Override
    public float getScale() {
        return 0.5f;
    }
}
