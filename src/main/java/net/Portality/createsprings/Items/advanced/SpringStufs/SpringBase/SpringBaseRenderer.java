package net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.Portality.createsprings.utill.CSpringsScrollValueHandler;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity.springAnimation;
import static net.Portality.createsprings.blocks.advanced.Spring.SpringVisual.SPRING_LEN;

public class SpringBaseRenderer extends CustomRenderedItemModelRenderer {
    protected final PartialModel Stress_Arrow = PartialModel.of(CreateSprings.asResource("item/drill/speedometer_arrow"));
    protected final PartialModel SPRING_PLATE = CSpringsPartalModels.SPRING_TOOL_SPRING_PLATE;
    protected final PartialModel SPRING_PIECE = CSpringsPartalModels.SPRING_TOOL_SPRING_PIECE;

    private static final RandomSource RANDOM = RandomSource.create();

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderBase(stack, model, renderer, transformType, ms, buffer, light, overlay);
    }

    public void renderBase(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                           PoseStack ms, MultiBufferSource buffer, int light, int overlay){
        renderer.render(model.getOriginalModel(), light);

        CompoundTag tag = stack.getOrCreateTag();

        double Speed = getSpeed(tag);

        if (tag.getFloat("Speed") > 5000){Speed += RANDOM.nextInt(-1, 1);}

        int Springs = tag.getInt("Springs_rn");

        ms.rotateAround(Axis.ZP.rotationDegrees(-(float) (Speed*1.8f)), 1.5f/16,4/16f,0);

        renderer.render(Stress_Arrow.get(), light);

        ms.rotateAround(Axis.ZP.rotationDegrees((float) (Speed*1.8f)), 1.5f/16,4/16f,0);

        ms.translate(0, 0, -2/16f);
        if (Springs == 2){
            ms.translate(6/16f, 0, 0);
            renderSpring(renderer, light, ms, tag.getFloat("Stored0"), tag);
            ms.translate(-12/16f, 0, 0);
            renderSpring(renderer, light, ms, tag.getFloat("Stored1"), tag);
            ms.translate(6/16f, 0, 0);
        } else if (Springs == 1){
            ms.translate(6/16f, 0, 0);
            renderSpring(renderer, light, ms, tag.getFloat("Stored0") + tag.getFloat("Stored1"), tag);
            ms.translate(-6/16f, 0, 0);
        }
        ms.translate(0, 0, 2/16f);
    }

    private void renderSpring( PartialItemModelRenderer renderer, int light, PoseStack ms, float stored, CompoundTag tag){
        float progress = 1 - (stored / Config.spring_capacity / 2f);

        if(tag.getBoolean("splash")){
            int phase = (AnimationTickHolder.getTicks() - tag.getInt("shiftTick")) % Config.spring_splash_duration + 1;
            progress = springAnimation(phase);
            float prevProgress = springAnimation(phase-1);
            progress = 1 - Mth.lerp(AnimationTickHolder.getPartialTicks(), prevProgress, progress);
        }

        renderer.render(SPRING_PLATE.get(), light);

        for(int i = 0; i < SPRING_LEN * 2; i++){
            renderer.render(SPRING_PIECE.get(), light);
            ms.translate(0, 0, 1/16f * progress * 0.5f);
            ms.rotateAround(Axis.ZP.rotationDegrees(90f), 0,0,0);
        }

        ms.translate(0, 0, 1/16f);
        renderer.render(SPRING_PLATE.get(), light);
        ms.translate(0, 0, -1/16f);

        ms.translate(0, 0, -(SPRING_LEN * 2)/16f * progress * 0.5f);
    }

    public static double getSpeed(CompoundTag tag){
        return Mth.lerp((AnimationTickHolder.getTicks() + AnimationTickHolder.getPartialTicks() - 1)
                % 40 / 40f ,tag.getFloat("LastSpeed") / 100, tag.getFloat("Speed") / 100);
    }
}
