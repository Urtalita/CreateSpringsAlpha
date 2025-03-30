package net.Portality.createsprings.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class AnimatedWelder extends AnimatedKinetics {
    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 24;

        blockElement(shaft(Direction.Axis.X))
                .rotateBlock(getWelderAngle(), 0, 0)
                .atLocal(-1.5, 1, 0)
                .scale(scale)
                .render(graphics);

        blockElement(ModBlocks.FRICTION_WELDER.getDefaultState())
                .rotateBlock(0, -90, 0)
                .atLocal(-1.5, 1, 0)
                .scale(scale)
                .render(graphics);

        blockElement(CSpringsPartalModels.WelderHead)
                .rotateBlock(0, 0 + getWelderAngle(), -90)
                .atLocal(-getAnimatedHeadOffset() / 2 -1.5, 1, 0)
                .scale(scale)
                .render(graphics);

        //////////////////////////////////////////////////////////////////////

        blockElement(shaft(Direction.Axis.X))
                .rotateBlock(-getWelderAngle(), 0, 0)
                .atLocal(1.5, 1, 0)
                .scale(scale)
                .render(graphics);

        blockElement(ModBlocks.FRICTION_WELDER.getDefaultState())
                .rotateBlock(0, 90, 0)
                .atLocal(1.5, 1, 0)
                .scale(scale)
                .render(graphics);

        blockElement(CSpringsPartalModels.WelderHead)
                .rotateBlock(0, 0 + getWelderAngle(), 90)
                .atLocal(getAnimatedHeadOffset() / 2 + 1.5, 1, 0)
                .scale(scale)
                .render(graphics);

        matrixStack.popPose();
    }

    private float getWelderAngle(){
        return (AnimationTickHolder.getRenderTime() * 32f) % 360;
    }

    private float getAnimatedHeadOffset() {
        float cycle = ((AnimationTickHolder.getRenderTime() - offset * 8) / 4) % 30;

        if (cycle < 10) {
            float progress = cycle / 10;
            return -(progress * progress * progress);
        }
        if (cycle < 15)
            return -1;
        if (cycle < 20)
            return -1 + (1 - ((20 - cycle) / 5));
        return 0;
    }
}
