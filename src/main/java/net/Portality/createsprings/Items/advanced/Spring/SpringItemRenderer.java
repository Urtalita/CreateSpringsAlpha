package net.Portality.createsprings.Items.advanced.Spring;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity.springAnimation;
import static net.Portality.createsprings.blocks.advanced.Spring.SpringVisual.SPRING_LEN;

public class SpringItemRenderer extends CustomRenderedItemModelRenderer {

    protected static final PartialModel PLATE = CSpringsPartalModels.SPRING_PLATE;
    protected static final PartialModel PIECE = CSpringsPartalModels.SPRING_PIECE;

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float su = GetStoredSu(stack);
        float progress = su/Config.spring_capacity;

        if(stack.getOrCreateTag().getBoolean("splash")){
            int phase = (AnimationTickHolder.getTicks() - stack.getOrCreateTag().getInt("shiftTick")) % Config.spring_splash_duration + 1;
            float nextprogress = springAnimation(phase);
            float prevProgress = springAnimation(phase-1);
            progress = Mth.lerp(AnimationTickHolder.getPartialTicks(), prevProgress, nextprogress);
        }

        ms.rotateAround(Axis.XP.rotationDegrees(180), 0,0,0);
        ms.translate(0, 0,  1/16f - 0.5);
        renderer.render(PLATE.get(), light);
        ms.translate(0, 0,  1/16f);
        ms.rotateAround(Axis.ZP.rotationDegrees(45), 0,0,0);

        for (int i = 0; i < SPRING_LEN; i++){
            ms.rotateAround(Axis.ZP.rotationDegrees(90), 0,0,0);
            ms.translate(0, 0,  (1-(progress/2f))/16f);
            renderer.render(PIECE.get(), light);
        }
        ms.rotateAround(Axis.ZP.rotationDegrees(-45), 0,0,0);
        ms.translate(0, 0,  1/16f);
        renderer.render(PLATE.get(), light);
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
