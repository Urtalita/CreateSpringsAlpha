package net.Portality.createsprings.Items.SpringStufs.SpringFan;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.Items.SpringStufs.SpringBase.SpringBaseRenderer;
import net.Portality.createsprings.Items.SpringStufs.SpringSpeedClientHandler;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringFanRenderer extends CustomRenderedItemModelRenderer {
    protected final PartialModel Head = CSpringsPartalModels.SPRING_FAN_HEAD;
    SpringBaseRenderer baseRenderer = new SpringBaseRenderer();

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        baseRenderer.renderBase(stack, model, renderer, transformType, ms, buffer, light, overlay);

        CompoundTag tag = stack.getOrCreateTag();

        double speed = SpringBaseRenderer.getSpeed(tag);
        float scroll = 0;

        if(Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == stack.getItem()){
            SpringSpeedClientHandler.updateSpeed(tag.getFloat("LastSpeed"), tag.getFloat("Speed"));
            scroll = SpringSpeedClientHandler.getScroll();
        }

        float zOffset = -1/16f;
        ms.translate(0, 0, -zOffset);
        ms.mulPose(Axis.ZP.rotationDegrees(scroll));
        ms.translate(0, 0, zOffset);

        renderer.render(Head.get(), light);
    }
}
