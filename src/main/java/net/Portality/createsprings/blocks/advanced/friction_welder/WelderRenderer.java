package net.Portality.createsprings.blocks.advanced.friction_welder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlock;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WelderRenderer extends KineticBlockEntityRenderer<SpringBlockEntity> {
    public WelderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void RenderCoils(BlockState state, Direction facing, float progress, int light, PoseStack ms, MultiBufferSource buffer){
        for (int j = 0; j < 3; j++){
            SuperByteBuffer CoilsRender = CachedBuffers.partialFacing(CSpringsPartalModels.SPRING_RING, state, facing);
            Vec3 offset = getCoilsOffset(progress, state, j);

            CoilsRender.translate(offset.x, offset.y, offset.z)
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    protected void renderSafe(SpringBlockEntity be, float pt, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, pt, ms, buffer, light, overlay);

        float progress = be.getProgress();

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(SpringBlock.FACING);
        Vec3 offset = getSpringOffset(progress, state);

        SuperByteBuffer plateRender = CachedBuffers.partialFacing(CSpringsPartalModels.SPRING_PLATE, state, facing);

        plateRender.translate(offset.x, offset.y, offset.z)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        RenderCoils(state, facing, progress, light, ms, buffer);

    }

    protected Vec3 getCoilsOffset(float progres, BlockState blockState, int number){
        return Vec3.atLowerCornerOf(blockState.getValue(DirectionalKineticBlock.FACING).getNormal()).scale((-1 *(number*4*(1-progres/2)-13) / 16));
    }

    protected Vec3 getSpringOffset(float progres, BlockState blockState) {
        return Vec3.atLowerCornerOf(blockState.getValue(DirectionalKineticBlock.FACING).getNormal()).scale(progres/2);
    }
}
