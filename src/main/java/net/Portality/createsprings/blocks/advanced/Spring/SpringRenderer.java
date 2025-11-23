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
        BlockPos pos = be.getBlockPos();
        Axis rotationAxis = getRotationAxis(facing);

        SuperByteBuffer plateRender = CachedBuffers.partialFacing(CSpringsPartalModels.SPRING_PLATE, blockState, facing);
        plateRender.translate(MoveToPos(1/16f, 8/16f, progress, facing, pos))
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        for (int ringIndex = 0; ringIndex < SPRING_LEN; ringIndex++) {
            float end = (8f+0.5f*ringIndex - (ringIndex % 4)/2f + 2)/16f;
            float start = (2f+ringIndex)/16f + 1/16f;

            SuperByteBuffer pieceRender = CachedBuffers.partialFacing(CSpringsPartalModels.SPRING_PIECE, blockState, facing);
            pieceRender.translate(MoveToPos(start, end, progress, facing, pos))
                    .light(light)
                    .rotateCenteredDegrees(45 + ringIndex * 90, rotationAxis)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    private Vec3 MoveToPos(float start, float end, float progress, Direction facing, BlockPos pos){
        Vec3i normalVi = facing.getOpposite().getNormal();
        Vec3 normal = new Vec3(normalVi.getX(), normalVi.getY(), normalVi.getZ());
        Vec3 ret = normal.scale(1 - Mth.lerp(progress, start, end));
        return ret.subtract(normal.scale(0.5));
    }

    @Override
    protected BlockState getRenderedBlockState(SpringBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}
