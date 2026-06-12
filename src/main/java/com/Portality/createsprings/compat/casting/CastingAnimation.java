package com.Portality.createsprings.compat.casting;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.server.fluid.CSpringsFluids;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;

public class CastingAnimation extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 100);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 20;

        blockElement(AllBlocks.SPOUT.getDefaultState())
                .scale(scale)
                .render(graphics);

        float cycle = (AnimationTickHolder.getRenderTime() - offset * 8) % 30;
        float squeeze = cycle < 20 ? Mth.sin((float) (cycle / 20f * Math.PI)) : 0;
        squeeze *= 20;

        matrixStack.pushPose();

        blockElement(AllPartialModels.SPOUT_TOP)
                .scale(scale)
                .render(graphics);
        matrixStack.translate(0, -3 * squeeze / 32f, 0);
        blockElement(AllPartialModels.SPOUT_MIDDLE)
                .scale(scale)
                .render(graphics);
        matrixStack.translate(0, -3 * squeeze / 32f, 0);
        blockElement(AllPartialModels.SPOUT_BOTTOM)
                .scale(scale)
                .render(graphics);
        matrixStack.translate(0, -3 * squeeze / 32f, 0);

        matrixStack.popPose();

        blockElement(CSpringsPartalModels.MOLD)
                .atLocal(0, 2, 0)
                .scale(scale)
                .render(graphics);

        AnimatedKinetics.DEFAULT_LIGHTING.applyLighting();
        matrixStack.pushPose();
        UIRenderHelper.flipForGuiRender(matrixStack);
        matrixStack.scale(16, 16, 16);
        float from = 3f / 16f;
        float to = 17f / 16f;
        FluidStack fluidStack = new FluidStack(CSpringsFluids.SPRING_ALLOY.get(),  1000);

        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, from, from, from, to, to, to, graphics.bufferSource(), matrixStack, LightTexture.FULL_BRIGHT, false, true);
        matrixStack.popPose();

        float width = 1 / 128f * squeeze;
        matrixStack.translate(scale / 2f, scale * 1.5f, scale / 2f);
        UIRenderHelper.flipForGuiRender(matrixStack);
        matrixStack.scale(16, 16, 16);
        matrixStack.translate(-0.5f, 0, -0.5f);
        from = -width / 2 + 0.5f;
        to = width / 2 + 0.5f;
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, from, 0, from, to, 2, to, graphics.bufferSource(), matrixStack, LightTexture.FULL_BRIGHT, false, true);
        graphics.flush();
        Lighting.setupFor3DItems();

        matrixStack.popPose();
    }
}
