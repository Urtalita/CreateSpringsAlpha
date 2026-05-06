package com.Portality.createsprings.blocks.advanced.kinetic_interface;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlock;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class KineticInterfaceRenderer extends SafeBlockEntityRenderer<KineticInterfaceBlockEntity> {
    public KineticInterfaceRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        BlockState blockState = context.state;
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        float renderPartialTicks = AnimationTickHolder.getPartialTicks();

        LerpedFloat animation = PortableStorageInterfaceMovement.getAnimation(context);
        float progress = animation.getValue(renderPartialTicks);
        boolean lit = animation.settled();
        render(blockState, lit, progress, matrices.getModel(),
                sbb -> sbb.light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                        .useLevelLight(context.world, matrices.getWorld())
                        .renderInto(matrices.getViewProjection(), vb));
    }

    @Override
    protected void renderSafe(KineticInterfaceBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        BlockState blockState = be.getBlockState();
        float progress = be.getExtensionDistance(partialTicks);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        render(blockState, be.isConnected(), progress, null, sbb -> sbb.light(light)
                .renderInto(ms, vb));
    }

    private static void render(BlockState blockState, boolean lit, float progress, PoseStack local,
                               Consumer<SuperByteBuffer> drawCallback) {
        SuperByteBuffer pulley = CachedBuffers.partial(CSpringsPartalModels.INTERFACE_PULLEY, blockState);
        SuperByteBuffer top = CachedBuffers.partial(CSpringsPartalModels.INTERFACE_TOP, blockState);

        if (local != null) {
            pulley.transform(local);
            top.transform(local);
        }
        Direction facing = blockState.getValue(PortableStorageInterfaceBlock.FACING);
        rotateToFacing(pulley, facing);
        rotateToFacing(top, facing);

        float offset = progress * 16 * 2;
        float pulleyOffset = offset / 16f / 2f;


        if(offset >= 8f && offset <= 12f){
            pulleyOffset = offset / 16 / 2;
            offset = 8;
        }
        if(offset > 12f){
            if(offset <= 22f){
                pulleyOffset = offset / 16 / 2;
            } else {
                pulleyOffset = 22f / 16f / 2f;
            }
            offset -= 6;
        }

        if(offset >= 12f){offset = 12;}

        offset = offset / 16f / 2f;

        pulley.translate(0, 0, -pulleyOffset);
        top.translate(0, 0, -offset);

        drawCallback.accept(pulley);
        drawCallback.accept(top);
    }

    private static void rotateToFacing(SuperByteBuffer buffer, Direction facing) {
        buffer.center()
                .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                .rotateXDegrees(facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 180)
                .uncenter();
    }
}
