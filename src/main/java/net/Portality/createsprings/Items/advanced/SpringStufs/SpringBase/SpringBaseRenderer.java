package net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.Portality.createsprings.utill.CSpringsScrollValueHandler;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringBaseRenderer extends CustomRenderedItemModelRenderer {
    protected final PartialModel Stress_Arrow = PartialModel.of(CreateSprings.asResource("item/drill/speedometer_arrow"));
    protected final PartialModel Right_Spring = PartialModel.of(CreateSprings.asResource("item/drill/right_spring"));
    protected final PartialModel Left_Spring = PartialModel.of(CreateSprings.asResource("item/drill/left_spring"));

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderBase(stack, model, renderer, transformType, ms, buffer, light, overlay);
    }

    public void renderBase(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                           PoseStack ms, MultiBufferSource buffer, int light, int overlay){
        renderer.render(model.getOriginalModel(), light);

        CompoundTag tag = stack.getOrCreateTag();

        double Speed = tag.getDouble("Speed") / 100;
        int Springs = tag.getInt("Springs_rn");

        float zOffset = -1/16f;

        ms.translate(0, 0, -zOffset);
        ms.rotateAround(Axis.ZP.rotationDegrees(-(float) (Speed*1.8f)), 1.5f/16,4/16f,0);
        ms.translate(0, 0, zOffset);

        renderer.render(Stress_Arrow.get(), light);

        ms.translate(0, 0, -zOffset);
        ms.rotateAround(Axis.ZP.rotationDegrees((float) (Speed*1.8f)), 1.5f/16,4/16f,0);
        ms.translate(0, 0, zOffset);

        if (Springs == 2){
            renderer.render(Right_Spring.get(), light);
            renderer.render(Left_Spring.get(), light);
        } else if (Springs == 1){
            renderer.render(Right_Spring.get(), light);
        }
    }
}
