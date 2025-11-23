package net.Portality.createsprings.Items.advanced.SpringStufs.SpringShowel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase.SpringBaseRenderer;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringShoveRenderer extends CustomRenderedItemModelRenderer {

    protected final PartialModel Head = CSpringsPartalModels.SPRING_SHOVE;
    SpringBaseRenderer baseRenderer = new SpringBaseRenderer();

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        baseRenderer.renderBase(stack, model, renderer, transformType, ms, buffer, light, overlay);

        CompoundTag tag = stack.getOrCreateTag();

        double speed = SpringBaseRenderer.getSpeed(tag);
        speed = SpringBaseRenderer.isTooFast(tag, speed);

        float scroll = (float) (AnimationTickHolder.getRenderTime() * speed * 2 % 360);

        float zOffset = -1/16f;
        ms.translate(0, 0, -zOffset);
        ms.mulPose(Axis.ZP.rotationDegrees(scroll));
        ms.translate(0, 0, zOffset);

        renderer.render(Head.get(), light);
    }
}
