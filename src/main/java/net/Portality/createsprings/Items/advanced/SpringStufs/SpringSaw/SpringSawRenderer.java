package net.Portality.createsprings.Items.advanced.SpringStufs.SpringSaw;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase.SpringBaseRenderer;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.Portality.createsprings.utill.CSpringsScrollValueHandler;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringSawRenderer extends CustomRenderedItemModelRenderer {
    protected final PartialModel SHAFT = CSpringsPartalModels.SAW_SHAFT;
    protected final PartialModel SHAFT_END = CSpringsPartalModels.SPRING_SAW;
    protected final PartialModel SAW_HEAD = CSpringsPartalModels.SAW_HEAD;
    SpringBaseRenderer baseRenderer = new SpringBaseRenderer();

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(model.getOriginalModel(), light);
        renderer.render(SHAFT_END.get(), light);

        baseRenderer.renderBase(stack, model, renderer, transformType, ms, buffer, light, overlay);

        CompoundTag tag = stack.getOrCreateTag();

        double Speed = tag.getDouble("Speed") / 100;
        float scroll = CSpringsScrollValueHandler.getScroll(stack, AnimationTickHolder.getPartialTicks(), (float) Speed) / 25f;

        float zOffset = -1/16f;
        ms.translate(0, 0, -zOffset);
        ms.mulPose(Axis.ZP.rotationDegrees(scroll));
        ms.translate(0, 0, zOffset);

        renderer.render(SHAFT.get(), light);

        ms.translate(0, 0, -zOffset);
        ms.mulPose(Axis.ZP.rotationDegrees(-scroll));
        ms.translate(0, 0, zOffset);

        ms.translate(0, 0, -zOffset);
        ms.rotateAround(Axis.XN.rotationDegrees(scroll), 0, 0, -13.5f/16f);
        ms.translate(0, 0, zOffset);

        renderer.render(SAW_HEAD.get(), light);
    }
}
