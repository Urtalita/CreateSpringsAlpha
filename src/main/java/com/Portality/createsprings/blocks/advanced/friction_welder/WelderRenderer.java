package com.Portality.createsprings.blocks.advanced.friction_welder;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class WelderRenderer extends KineticBlockEntityRenderer<WelderBlockEntity> {
    public WelderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(WelderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = be.getCraftState();
        if(state != null){
            PoseStack msLocal = new PoseStack();
            var msr = TransformStack.of(msLocal);

            msr.center();

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            BlockState craftState = be.getCraftState();
            if(craftState != null){
                ItemStack item = craftState.getBlock().asItem().getDefaultInstance();

                BakedModel bakedModel = itemRenderer.getModel(item, null, null, 0);

                ms.pushPose();
                float itemScale = 2f;
                Vec3 trVec = MoveWithoutVectors(be.getHeadMove(partialTicks) + 1, be.movementDirection);
                msLocal.translate(trVec.x, trVec.y, trVec.z);

                Direction.Axis axis = be.getBlockState().getValue(FACING).getAxis();
                if(axis == Direction.Axis.X) msLocal.rotateAround(Axis.XN.rotationDegrees(-be.getInterpolatedAngle(partialTicks)), 0, 0, 0);
                if(axis == Direction.Axis.Y) msLocal.rotateAround(Axis.YP.rotationDegrees(be.getInterpolatedAngle(partialTicks)), 0, 0, 0);
                if(axis == Direction.Axis.Z) msLocal.rotateAround(Axis.ZP.rotationDegrees(be.getInterpolatedAngle(partialTicks)), 0, 0, 0);

                msLocal.scale(itemScale, itemScale, itemScale);

                ms.last().pose().mul(msLocal.last().pose());

                itemRenderer.render(item, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, bakedModel);
                ms.popPose();
            }
        }

        if (VisualizationManager.supportsVisualization(be.getLevel())) return;

        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        final Direction facing = be.getBlockState()
                .getValue(BlockStateProperties.FACING);
        PartialModel top = CSpringsPartalModels.WelderHead;
        SuperByteBuffer superBuffer = CachedBuffers.partial(top, be.getBlockState());

        float interpolatedAngle = be.getInterpolatedAngle(partialTicks - 1);
        superBuffer.translate(MoveWithoutVectors(be.getHeadMove(partialTicks), facing.getOpposite().getNormal()));
        kineticRotationTransform(superBuffer, be, facing.getAxis(), (float) (interpolatedAngle / 180 * Math.PI), light);

        if (facing.getAxis()
                .isHorizontal())
            superBuffer.rotateCentered(
                    AngleHelper.rad(AngleHelper.horizontalAngle(facing.getOpposite())), Direction.UP);
        superBuffer.rotateCentered(AngleHelper.rad(-90 - AngleHelper.verticalAngle(facing)), Direction.EAST);
        superBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    protected SuperByteBuffer getRotatedModel(WelderBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, state.getValue(FACING).getOpposite());
    }

    private Vec3 MoveWithoutVectors(float Moving, Vec3i movementDirection){
        float offset = 1 - Moving - 0.5f;
        return new Vec3(
                (movementDirection.getX() * offset),
                (movementDirection.getY() * offset),
                (movementDirection.getZ() * offset)
        );
    }
}
