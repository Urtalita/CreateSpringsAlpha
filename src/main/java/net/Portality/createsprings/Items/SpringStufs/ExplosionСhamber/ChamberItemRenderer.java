package net.Portality.createsprings.Items.SpringStufs.ExplosionСhamber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static net.Portality.createsprings.Items.SpringStufs.SpringBase.SpringBaseRenderer.renderSpring;
import static net.Portality.createsprings.blocks.advanced.Spring.SpringVisual.SPRING_LEN;

public class ChamberItemRenderer extends CustomRenderedItemModelRenderer {
    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(model.getOriginalModel(), light);
        CompoundTag tag = stack.getOrCreateTag();
        int fuel = tag.getInt("fuel");

        if(fuel != 0){renderer.render(CSpringsPartalModels.CHAMBER_GUNPOWDER.get(), light);}

        if(tag.getInt("Springs_rn") == 1){
            ms.rotateAround(Axis.XP.rotationDegrees(90f), 0,0,0);
            ms.translate(0, 0, 3/16f);
            renderSpring(renderer, light, ms, tag.getFloat("Stored0"), tag, SPRING_LEN, CSpringsPartalModels.CHAMBER_SPRING_PLATE, CSpringsPartalModels.CHAMBER_SPRING_PIECE);
        }
    }
}
