package net.Portality.createsprings.blocks.advanced.SpringCatapult;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.Portality.createsprings.blocks.advanced.Spring.SpringRenderer;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringCatapultRenderer extends SmartBlockEntityRenderer<SpringCatapultBlockEntity> {
    public SpringCatapultRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SpringCatapultBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        renderItem(be, partialTicks, ms, buffer, light, overlay);

        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        SpringRenderer.renderSpring(ms, light, buffer, be.getBlockState(), be.getYAngle(partialTicks), be.getXAngle(partialTicks), be.getProgress(partialTicks));
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
}
