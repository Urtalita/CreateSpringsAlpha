package net.Portality.createsprings.Items.advanced.Spring;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.CreateSprings;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringItemRenderer extends CustomRenderedItemModelRenderer {

    protected static final PartialModel half = PartialModel.of(CreateSprings.asResource("block/spring/springitem_half_compresed"));
    protected static final PartialModel full = PartialModel.of(CreateSprings.asResource("block/spring/springitem_compresed"));

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        float su = GetStoredSu(stack);
        float capacity = CreateSprings.SPRING_CAPACITY;
        float progress = su / capacity;

        if (progress< 0.5){
            renderer.render(model.getOriginalModel(), light);
        } else if(progress < 1){
            renderer.render(half.get(), light);
        } else if(progress == 1){
            renderer.render(full.get(),light);
        } else {
            renderer.render(model.getOriginalModel(),light);
        }
    }

    public float GetStoredSu(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        float stored = -1;

        if (tag.contains("BlockEntityTag")){
            CompoundTag BlockEntityTag = tag.getCompound("BlockEntityTag");
            stored = BlockEntityTag.getFloat("Stored");
        }

        return stored;
    }
}
