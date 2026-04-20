package com.Portality.createsprings.items.SpringStufs.SpringShowel;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.items.SpringStufs.SpringBase.SpringBaseRenderer;
import com.Portality.createsprings.items.SpringStufs.SpringSpeedClientHandler;
import com.Portality.createsprings.items.SpringStufs.SpringSpeedSys;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringShoveRenderer extends CustomRenderedItemModelRenderer {

    protected final PartialModel Head = CSpringsPartalModels.SPRING_SHOVE;
    SpringBaseRenderer baseRenderer = new SpringBaseRenderer();

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        baseRenderer.renderBase(stack, model, renderer, transformType, ms, buffer, light, overlay);

        float scroll = 0;

        if(Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == stack.getItem()){
            SpringSpeedClientHandler.updateSpeed(SpringSpeedSys.getLastSpeed(stack), SpringSpeedSys.getRealSpeed(stack));
            scroll = SpringSpeedClientHandler.getScroll();
        }

        float zOffset = -1/16f;
        ms.translate(0, 0, -zOffset);
        ms.mulPose(Axis.ZP.rotationDegrees(scroll));
        ms.translate(0, 0, zOffset);

        renderer.render(Head.get(), light);
    }
}
