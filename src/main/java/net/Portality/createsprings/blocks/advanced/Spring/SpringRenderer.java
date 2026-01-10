package net.Portality.createsprings.blocks.advanced.Spring;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static net.Portality.createsprings.blocks.advanced.Spring.SpringVisual.SPRING_LEN;
import static net.Portality.createsprings.utill.Helpers.RenderHelper.MoveToPos;
import static net.Portality.createsprings.utill.Helpers.RenderHelper.getRotationAxis;

public class SpringRenderer extends KineticBlockEntityRenderer<SpringBlockEntity> {

    public SpringRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(SpringBlockEntity be) {
        return true;
    }

    @Override
    protected void renderSafe(SpringBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        BlockState blockState = be.getBlockState();
        float progress = be.getProgress(partialTicks);
        Direction facing = be.getBlockState().getValue(DirectionalKineticBlock.FACING);
        Axis rotationAxis = getRotationAxis(facing);

        SuperByteBuffer plateRender = CachedBuffers.partialFacing(CSpringsPartalModels.SPRING_PLATE, blockState, facing);
        plateRender.translate(MoveToPos(1/16f, 8/16f, progress, facing))
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        for (int ringIndex = 0; ringIndex < SPRING_LEN; ringIndex++) {
            float end = (8f+0.5f*ringIndex - (ringIndex % 4)/2f + 2)/16f;
            float start = (2f+ringIndex)/16f + 1/16f;

            SuperByteBuffer pieceRender = CachedBuffers.partialFacing(CSpringsPartalModels.SPRING_PIECE, blockState, facing);
            pieceRender.translate(MoveToPos(start, end, progress, facing))
                    .light(light)
                    .rotateCenteredDegrees(45 + ringIndex * 90, rotationAxis)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    public static void renderSpring(PoseStack ms, int light, MultiBufferSource buffer, BlockState state, float yRot, float xRot, float progress, Vec3 offset){
        double horizontalAngleRad = Math.toRadians(yRot);
        double verticalAngleRad = Math.toRadians(xRot);

        double x = Math.sin(horizontalAngleRad) * Math.cos(verticalAngleRad);
        double y = Math.sin(verticalAngleRad);
        double z = Math.cos(horizontalAngleRad) * Math.cos(verticalAngleRad);

        Vec3 movementDirection = new Vec3(x, y, z);

        movementDirection = movementDirection.scale(-1);

        SuperByteBuffer plateRender = CachedBuffers.partial(CSpringsPartalModels.SPRING_PLATE, state);
        plateRender.translate(MoveToPos(1/16f, 8/16f, progress, movementDirection))
                .translate(offset)
                .rotateCenteredDegrees(yRot, Direction.Axis.Y)
                .rotateCenteredDegrees(-xRot , Direction.Axis.X)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        SuperByteBuffer plateRender2 = CachedBuffers.partial(CSpringsPartalModels.SPRING_PLATE, state);
        plateRender2.translate(offset)
                .rotateCenteredDegrees(yRot, Direction.Axis.Y)
                .rotateCenteredDegrees(-xRot , Direction.Axis.X)
                .light(light)
                .translate(new Vec3(0, 0, 7/16f))
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        for (int ringIndex = 0; ringIndex < SPRING_LEN; ringIndex++) {
            float end = (8f+0.5f*ringIndex - (ringIndex % 4)/2f + 2)/16f;
            float start = (2f+ringIndex)/16f + 1/16f;

            SuperByteBuffer pieceRender = CachedBuffers.partial(CSpringsPartalModels.SPRING_PIECE, state);
            pieceRender.translate(MoveToPos(start, end, progress, movementDirection))
                    .translate(offset)
                    .light(light)
                    .rotateCenteredDegrees(yRot, Direction.Axis.Y)
                    .rotateCenteredDegrees(-xRot , Direction.Axis.X)
                    .rotateCenteredDegrees(45 + ringIndex * 90, Direction.Axis.Z)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    public static Vec3 MoveToPos(float start, float end, float progress, Direction facing){
        Vec3i normalVi = facing.getOpposite().getNormal();
        Vec3 normal = new Vec3(normalVi.getX(), normalVi.getY(), normalVi.getZ());
        Vec3 ret = normal.scale(1 - Mth.lerp(progress, start, end));
        return ret.subtract(normal.scale(0.5));
    }

    private static Vec3 MoveToPos(float start, float end, float progress, Vec3 movementDirection){
        Vec3 ret = movementDirection.scale(1 - Mth.lerp(progress, start, end));
        return ret.subtract(movementDirection.scale(0.5));
    }

    @Override
    protected BlockState getRenderedBlockState(SpringBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}
