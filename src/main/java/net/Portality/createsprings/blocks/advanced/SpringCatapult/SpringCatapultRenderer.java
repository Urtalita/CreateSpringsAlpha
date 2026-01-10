package net.Portality.createsprings.blocks.advanced.SpringCatapult;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.Portality.createsprings.blocks.advanced.Spring.SpringRenderer;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlock;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SpringCatapultRenderer extends KineticBlockEntityRenderer<SpringCatapultBlockEntity> {
    public SpringCatapultRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SpringCatapultBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        renderItem(be, partialTicks, ms, buffer, light, overlay);
        BlockState state = be.getBlockState();
        Vec3 offset = new Vec3(0, 28/16f, 0);
        Vec3 connectionOffset = new Vec3(0, 1, 0);

        float xRot = be.getXAngle(partialTicks);
        float yRot = be.getYAngle(partialTicks);

        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        if(state.getValue(SpringCatapultBlock.CEILING)){
            offset = offset.scale(-1);
            connectionOffset = connectionOffset.scale(-1);
        }

        SpringRenderer.renderSpring(ms, light, buffer, state, yRot, xRot, be.getProgress(partialTicks), offset);

        SuperByteBuffer springHolderRenderer = CachedBuffers.partial(CSpringsPartalModels.SPRING_CATAPULT_HOLDER, state);
        springHolderRenderer.translate(offset)
                .rotateCenteredDegrees(-90, Direction.Axis.Y)
                .rotateCenteredDegrees(yRot, Direction.Axis.Y)
                .rotateCenteredDegrees(xRot , Direction.Axis.Z)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        SuperByteBuffer connectionsRenderer = CachedBuffers.partial(CSpringsPartalModels.SPRING_CATAPULT_CONNECTION, state);
        connectionsRenderer.translate(connectionOffset)
                .rotateCenteredDegrees(-90, Direction.Axis.Y)
                .rotateCenteredDegrees(yRot, Direction.Axis.Y)
                .rotateCenteredDegrees((state.getValue(SpringCatapultBlock.CEILING)) ? 180 : 0, Direction.Axis.X)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    public boolean shouldRenderOffScreen(SpringCatapultBlockEntity be) {
        return true;
    }


    private void renderItem(SpringCatapultBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay){
        PoseStack msLocal = new PoseStack();
        var msr = TransformStack.of(msLocal);

        msr.center();

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack item = be.heldStack;
        boolean hasItem = item != null;

        int phase = be.getPhase();

        BakedModel bakedModel = itemRenderer.getModel(item, be.getLevel(), null, 0);
        boolean isBlockItem = hasItem && (item.getItem() instanceof BlockItem) && bakedModel.isGui3d();

        ms.pushPose();
        float itemScale = isBlockItem ? .5f : .625f;
        //msLocal.translate(0, isBlockItem ? -9 / 16f : -10 / 16f, 0);

        if(be.isUpsideDown()){
            msLocal.translate(0, -28 / 16f, 0);
        } else {
            msLocal.translate(0, 28 / 16f, 0);
        }

        msr.rotateYDegrees(be.getYAngle(partialTicks));

        if(phase == 0){
            msr.rotateXDegrees(-be.getXAngle(partialTicks));
            msLocal.translate( 0, 0, -(1 - be.getProgress(partialTicks)) / 2);
        } else {
            if(be.launcher != null){
                CatapultLauncher launcher = be.launcher;
                msLocal.translate(0, launcher.getYInterpolated(phase - 1, partialTicks),-1 * launcher.getXInterpolated(phase - 1, partialTicks));
                msr.rotateXDegrees(be.launcher.getRotationAngle(phase - 1, partialTicks));
            }
        }

        msLocal.scale(itemScale, itemScale, itemScale);

        ms.last().pose().mul(msLocal.last().pose());

        itemRenderer.render(item, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, bakedModel);
        ms.popPose();
    }

    @Override
    protected SuperByteBuffer getRotatedModel(SpringCatapultBlockEntity be, BlockState state) {
        Direction facing = (state.getValue(SpringCatapultBlock.CEILING)) ? Direction.UP : Direction.DOWN;
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, facing);
    }
}
