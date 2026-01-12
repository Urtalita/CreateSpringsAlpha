package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.Portality.createsprings.config.ModConfigs;
import net.Portality.createsprings.utill.Helpers.RenderHelper;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

import static net.Portality.createsprings.blocks.advanced.Spring.SpringRenderer.MoveToPos;
import static net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringVisual.calculateLen;
import static net.Portality.createsprings.utill.Helpers.RenderHelper.getRotationAxis;

public class LargeSpringRenderer extends ShaftRenderer<LargeSpringBlockEntity> {
    public LargeSpringRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(LargeSpringBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    protected void renderSafe(LargeSpringBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        BlockState blockState = be.getBlockState();
        float progress = 1 - be.getProgres(partialTicks);
        Direction facing = be.getBlockState().getValue(DirectionalKineticBlock.FACING);
        Direction.Axis rotationAxis = facing.getAxis();
        int len = be.getLen() * 4;

        SuperByteBuffer plateRender = CachedBuffers.partialFacing(CSpringsPartalModels.LARGE_SPRING_PLATE, blockState);
        plateRender.light(light).translate(setPos(0, 0, -7/16f, facing)).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        SuperByteBuffer UPPlateRender = CachedBuffers.partialFacing(CSpringsPartalModels.LARGE_SPRING_PLATE, blockState);
        UPPlateRender.light(light).translate(setPos(0, 0,calculateLen(progress, len) - 4/16f*(1-progress), facing)).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        for (int i = 0; i < len; i++) {
            SuperByteBuffer RingRender = CachedBuffers.partial(CSpringsPartalModels.LARGE_SPRING_COIL_ROTATED, blockState);
            RingRender.light(light)
                    .translate(updateRingPosition(i, progress, facing))
                    .rotateCenteredDegrees(90 * i, rotationAxis);

            SuperByteBuffer RingCornerRender = CachedBuffers.partial(CSpringsPartalModels.LARGE_SPRING_COIL_CORNER, blockState);
            RingCornerRender.light(light)
                    .translate(updateCornerPosition(i, progress, facing));

            if(facing.getAxis() == Direction.Axis.X){
                RingCornerRender.rotateZCenteredDegrees(90);
                RingCornerRender.rotateYCenteredDegrees(90 * i);
                if(i % 2 == 1) RingCornerRender.rotateYCenteredDegrees(180);
            } else if (facing.getAxis() == Direction.Axis.Z){
                RingCornerRender.rotateXCenteredDegrees(90);
                RingCornerRender.rotateYCenteredDegrees(90 * i + 90);

                RingRender.rotateXCenteredDegrees(90);
            } else if (facing.getAxis() == Direction.Axis.Y){
                RingCornerRender.rotateYCenteredDegrees(90 * i);
                if(i % 2 == 1) RingCornerRender.rotateYCenteredDegrees(180);
            }

            RingCornerRender.renderInto(ms, buffer.getBuffer(RenderType.solid()));
            RingRender.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    public static void renderSpringInContraption(
            MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer, Float theoreticalStored){
        BlockState blockState = context.state;

        float stored = Objects.requireNonNullElseGet(theoreticalStored, () -> context.blockEntityData.getFloat("stored"));
        float capacity = context.blockEntityData.getFloat("capacity");
        float progress = 1 - stored / capacity;

        Direction facing = blockState.getValue(DirectionalKineticBlock.FACING);
        Direction facingInverted = facing.getOpposite();
        Direction.Axis rotationAxis = facing.getAxis();
        int len = context.blockEntityData.getInt("len") * 4;
        PoseStack m = matrices.getModel();
        VertexConsumer builder = buffer.getBuffer(RenderType.solid());

        SuperByteBuffer plateRender = CachedBuffers.partialFacing(CSpringsPartalModels.LARGE_SPRING_PLATE, blockState, facingInverted);
        plateRender.transform(m);
        plateRender.translate(setPos(0, 0, -7/16f, facing));
        plateRender.light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                .useLevelLight(context.world, matrices.getWorld())
                .renderInto(matrices.getViewProjection(), builder);

        SuperByteBuffer UPPlateRender = CachedBuffers.partialFacing(CSpringsPartalModels.LARGE_SPRING_PLATE, blockState, facingInverted);
        UPPlateRender.transform(m);
        UPPlateRender.translate(setPos(0, 0,calculateLen(progress, len) - 4/16f*(1-progress), facing));
        UPPlateRender.light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                .useLevelLight(context.world, matrices.getWorld())
                .renderInto(matrices.getViewProjection(), builder);

        for (int i = 0; i < len; i++) {
            SuperByteBuffer RingRender = CachedBuffers.partialFacing(CSpringsPartalModels.LARGE_SPRING_COIL_ROTATED, blockState, facingInverted);
            RingRender.transform(m);
            SuperByteBuffer RingCornerRender = CachedBuffers.partialFacing(CSpringsPartalModels.LARGE_SPRING_COIL_CORNER, blockState, facingInverted);
            RingCornerRender.transform(m);

            RingCornerRender.translate(updateCornerPosition(i, progress, facing));
            RingCornerRender.rotateCenteredDegrees(90, RenderHelper.getSecondPerpendicularAxis(facing));
            RingCornerRender.light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                    .useLevelLight(context.world, matrices.getWorld())
                    .renderInto(matrices.getViewProjection(), builder);

            RingRender.translate(updateRingPosition(i, progress, facing))
                    .rotateCenteredDegrees(-90 * i - 90, rotationAxis);
            RingRender.rotateCenteredDegrees(90, RenderHelper.getPerpendicularAxis(facing));
            RingRender.light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                    .useLevelLight(context.world, matrices.getWorld())
                    .renderInto(matrices.getViewProjection(), builder);
        }
    }

    private static Vec3 updateRingPosition(int ringIndex, float progres, Direction facing) {
        int x = 0;
        int z = 0;

        int index = ringIndex % 4;
        switch (index){
            case 0: x = 1; break;
            case 1: z = 1; break;
            case 2: x = -1; break;
            case 3: z = -1; break;
        }

        return setPos(x, z, calculateLen(progres, ringIndex), facing);
    }

    private static Vec3 updateCornerPosition(int ringIndex, float progres, Direction facing) {
        int x = 0;
        int z = 0;

        int index = ringIndex % 4;
        switch (index){
            case 0: x = 1;z = 1; break;
            case 1: x = -1;z = 1; break;
            case 2: x = -1;z = -1; break;
            case 3: x = 1;z = -1; break;
        }

        return setPos(x, z, calculateLen(progres, ringIndex), facing);
    }

    private int applyBaseRotationCorner(Direction facing , int index){
        Direction.Axis axis = facing.getAxis();
        if(axis == Direction.Axis.Z){
            return 90 * index;
        } else if (axis == Direction.Axis.X){
            return 90 * index - 90;
        }
        if(index % 4 == 1){
            return -90;
        } else if(index % 4 == 3){
            return 90;
        }
        return 90 * index;
    }

    private static Vec3 setPos(int x, int z, float len, Direction facing){
        float dierectionFactor = 1;
        if(facing == Direction.DOWN || facing == Direction.WEST || facing == Direction.NORTH){
            dierectionFactor = -1;
        }
        if(facing == Direction.UP || facing == Direction.DOWN){
            return new Vec3(
                    (x),
                    (len * dierectionFactor),
                    (z)
            );
        } else if(facing == Direction.EAST || facing == Direction.WEST){
            return new Vec3(
                    (len * dierectionFactor),
                    (x),
                    (z)
            );
        }
        return new Vec3(
                (x),
                (z),
                (len * dierectionFactor)
        );
    }
}
