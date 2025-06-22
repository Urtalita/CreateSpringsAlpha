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
import static net.Portality.createsprings.utill.CSpringsPartalModels.SPRING_PIECE;
import static net.Portality.createsprings.utill.CSpringsPartalModels.SPRING_PLATE;

public class SpringBaseRenderer extends CustomRenderedItemModelRenderer {
    protected final PartialModel Stress_Arrow = PartialModel.of(CreateSprings.asResource("item/drill/speedometer_arrow"));

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
            renderSmallSpring(renderer, light, ms, tag.getFloat("Stored0"), tag, SPRING_LEN*2);
            ms.translate(-12/16f, 0, 0);
            renderSmallSpring(renderer, light, ms, tag.getFloat("Stored1"), tag, SPRING_LEN*2);
            ms.translate(6/16f, 0, 0);
        } else if (Springs == 1){
            ms.translate(6/16f, 0, 0);
            renderSmallSpring(renderer, light, ms, tag.getFloat("Stored0") + tag.getFloat("Stored1"), tag, SPRING_LEN*2);
            ms.translate(-6/16f, 0, 0);
        }
        ms.translate(0, 0, 2/16f);
    }

    public static void renderSmallSpring(PartialItemModelRenderer renderer, int light, PoseStack ms, float stored, CompoundTag tag, int springLen){
        renderSpring(renderer, light, ms, stored, tag, springLen, CSpringsPartalModels.SPRING_TOOL_SPRING_PLATE, CSpringsPartalModels.SPRING_TOOL_SPRING_PIECE);
    }

    public static void renderTinySpring(PartialItemModelRenderer renderer, int light, PoseStack ms, float stored, CompoundTag tag, int springLen){
        renderSpring(renderer, light, ms, stored, tag, springLen, CSpringsPartalModels.CHAMBER_SPRING_PLATE, CSpringsPartalModels.CHAMBER_SPRING_PIECE);
    }

    public static void renderSpring(PartialItemModelRenderer renderer, int light, PoseStack ms, float stored, CompoundTag tag
            , int springLen, PartialModel plate, PartialModel piece){
        float progress = 1 - (stored / Config.spring_capacity / 2f);

        if(tag.getBoolean("splash")){
            int phase = (AnimationTickHolder.getTicks() - tag.getInt("shiftTick")) % Config.spring_splash_duration + 1;
            progress = springAnimation(phase);
            float prevProgress = springAnimation(phase-1);
            progress = 1 - Mth.lerp(AnimationTickHolder.getPartialTicks(), prevProgress, progress);
        }

        renderer.render(plate.get(), light);

        for(int i = 0; i < springLen; i++){
            renderer.render(piece.get(), light);
            ms.translate(0, 0, 1/16f * progress * 0.5f);
            ms.rotateAround(Axis.ZP.rotationDegrees(90f), 0,0,0);
        }

        ms.translate(0, 0, 1/16f);
        renderer.render(plate.get(), light);
        ms.translate(0, 0, -1/16f);

        ms.translate(0, 0, -(springLen)/16f * progress * 0.5f);
    }

    public static double getSpeed(CompoundTag tag){
        return Mth.lerp((AnimationTickHolder.getTicks() + AnimationTickHolder.getPartialTicks() - 1)
                % 40 / 40f ,tag.getFloat("LastSpeed") / 100, tag.getFloat("Speed") / 100);
    }
}
