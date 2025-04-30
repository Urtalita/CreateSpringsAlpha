package net.Portality.createsprings.Items.advanced.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HatRenderer extends CustomRenderedItemModelRenderer {
    protected final PartialModel hat = CSpringsPartalModels.HAT;

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if(stack.getEquipmentSlot() == EquipmentSlot.HEAD){
            float zOffset = -1/16f;
            ms.translate(0, 17/16f, -zOffset);

            renderer.render(hat.get(), light);
        }
    }
}
