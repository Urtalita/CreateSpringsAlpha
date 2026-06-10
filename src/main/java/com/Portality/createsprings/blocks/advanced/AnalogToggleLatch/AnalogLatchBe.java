package com.Portality.createsprings.blocks.advanced.AnalogToggleLatch;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;

import java.util.List;

public class AnalogLatchBe extends SmartBlockEntity {
    public ScrollValueBehaviour analogValue;
    int lastChange;
    LerpedFloat clientState;

    public AnalogLatchBe(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        analogValue = new ScrollValueBehaviour(Component.translatable("analog_value"),
                this, new AnalogValueBoxTransform());
        analogValue.between(1, 15);
        analogValue.value = 15;
        analogValue.withCallback(this::updateValue);

        behaviours.add(analogValue);
    }

    private void updateValue(int value){
        sendData();
        setChanged();
        this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
    }

    public int getValue(){
        return analogValue.getValue();
    }

    public float getInterpolatedValue(float pt){
        return Mth.lerp(pt, lastChange, getValue());
    }

    @Override
    public void tick() {
        if(level.isClientSide()){
            lastChange = getValue();
        }
        super.tick();
    }

    private static class AnalogValueBoxTransform extends ValueBoxTransform {

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            Direction direction = state.getValue(AnalogLatchBlock.FACING);
            Vec3 location = VecHelper.voxelSpace(8, 4, 13);
            location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(direction), Direction.Axis.Y);
            return location;
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            float yRot = 180 - state.getValue(AnalogLatchBlock.FACING).toYRot();
            float xRot = 90;
            TransformStack.of(ms)
                    .rotateYDegrees(yRot)
                    .rotateXDegrees(xRot);
        }

        @Override
        public float getScale() {
            return 0.5f;
        }

    }
}
