package net.Portality.createsprings.Items.advanced.hat.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ItemStack;
import static net.Portality.createsprings.Items.advanced.hat.HatItem.readStackFromNBT;

public class HatRenderer extends CustomRenderedItemModelRenderer {
    protected static final PartialModel hat = CSpringsPartalModels.HAT;
    protected static final PartialModel hat2 = CSpringsPartalModels.HAT2;

    protected final PartialModel googles = AllPartialModels.GOGGLES;
    public static final float duration = 90;

    @Override
    public void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                       PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        if(stack.getEquipmentSlot() == EquipmentSlot.HEAD){
            CompoundTag tag = stack.getOrCreateTag();
            Item item = readStackFromNBT(stack).getItem();
            ItemStack innerStack = readStackFromNBT(stack);

            if(item != Items.AIR){
                if(item instanceof BlockItem){
                    renderItem(stack, ms, renderer, light);
                    ms.translate(0, 3/16f, 0);
                }
            }

            if(tag.getBoolean("animation")){

                if(item != Items.AIR){
                    if(!(item instanceof BlockItem)){
                        renderItem(stack, ms, renderer, light);
                    }
                }

                float time = getTime(tag);
                float rotation = time;
                if(rotation <= -(duration/2)){rotation = (duration - time * -1) * -1;}
                ms.rotateAround(Axis.ZP.rotationDegrees((rotation)), 6/16f, -13/16f, 0);
                if(time * -1 > (duration - 2)){tag.putBoolean("animation", false);}
            }
            ms.translate(0, -1/16f, 0);

            renderer.render(hat.get(), light);
            renderer.render(hat2.get(), Sheets.cutoutBlockSheet(), light);

            ms.translate(0, -2.5/16f, 0);
            if(tag.getBoolean("goggles")){
                renderer.render(googles.get(), light);
            }
        }
    }

    private void renderItem(ItemStack stack, PoseStack ms, PartialItemModelRenderer renderer, int light){
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model1 = itemRenderer.getModel(readStackFromNBT(stack), Minecraft.getInstance().level, null, 0);
        if(!model1.isCustomRenderer()){
            ms.translate(0, -4/16f, 0);
            ms.scale(0.5f, 0.5f, 0.5f);
            renderer.render(model1, light);
            ms.scale(2, 2, 2);
            ms.translate(0, 4/16f, 0);
        }
    }

    private float getTime(CompoundTag tag){
        return ((AnimationTickHolder.getTicks() - tag.getInt("animation_tick")) % duration + AnimationTickHolder.getPartialTicks() - 1) * -1 * 2;
    }
}
