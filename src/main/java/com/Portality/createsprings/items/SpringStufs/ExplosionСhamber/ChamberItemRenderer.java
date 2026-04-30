package com.Portality.createsprings.items.SpringStufs.ExplosionСhamber;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static com.Portality.createsprings.blocks.advanced.spring.SpringInstance.SPRING_LEN;
import static com.Portality.createsprings.blocks.advanced.spring.SpringRenderer.renderSpring;
import static com.Portality.createsprings.items.SpringStufs.SpringBase.SpringBaseRenderer.renderSmallSpring;
import static com.Portality.createsprings.items.SpringStufs.SpringBase.SpringBaseRenderer.renderTinySpring;


public class ChamberItemRenderer extends CustomRenderedItemModelRenderer {
    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(model.getOriginalModel(), light);
        float fuel = ChamberItem.getFuel(stack);

        if(fuel != 0){renderer.render(CSpringsPartalModels.CHAMBER_GUNPOWDER.get(), light);}

        var list = stack.get(CSpringsDataComponents.STORED_LIST);
        if(SpringPoweredCore.getSprings(stack) > 0){
            ms.rotateAround(Axis.XP.rotationDegrees(90f), 0,0,0);
            ms.translate(0, 0, 3/16f);
            renderTinySpring(renderer, light, ms, list.get(0), SPRING_LEN);
        }
    }
}
