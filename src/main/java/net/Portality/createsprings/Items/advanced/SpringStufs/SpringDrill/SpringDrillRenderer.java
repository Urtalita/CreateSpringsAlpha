package net.Portality.createsprings.Items.advanced.SpringStufs.SpringDrill;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase.SpringBaseRenderer;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringDrillRenderer extends CustomRenderedItemModelRenderer {

    protected final PartialModel Head = CSpringsPartalModels.SPRING_DRILL_HEAD;
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
