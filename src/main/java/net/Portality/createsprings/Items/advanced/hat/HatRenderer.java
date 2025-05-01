package net.Portality.createsprings.Items.advanced.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HatRenderer extends CustomRenderedItemModelRenderer {
    protected final PartialModel hat = CSpringsPartalModels.HAT;
    public static final float duration = 90;

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if(stack.getEquipmentSlot() == EquipmentSlot.HEAD){
            CompoundTag tag = stack.getOrCreateTag();
            if(tag.getBoolean("animation")){
                float time = getTime(tag);
                float rotation = time;
                if(rotation <= -(duration/2)){rotation = (duration - time * -1) * -1;}
                ms.rotateAround(Axis.ZP.rotationDegrees((rotation)), 6/16f, -13/16f, 0);
                if(time * -1 > (duration - 2)){tag.putBoolean("animation", false);}
            }
            renderer.render(hat.get(), light);
        }
    }

    private float getTime(CompoundTag tag){
        return ((AnimationTickHolder.getTicks() - tag.getInt("animation_tick")) % duration + AnimationTickHolder.getPartialTicks() - 1) * -1 * 2;
    }
}
