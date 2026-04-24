package com.Portality.createsprings.mixins.banner;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.client.CSpringsGuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public class MixinCreativeModeInventoryScreen {
    @Shadow
    private static CreativeModeTab selectedTab;

    @Inject(
            method = "renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V",
            at = @At("TAIL")
    )
    private void drawCustomBanner(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        CreativeModeInventoryScreen gui = (CreativeModeInventoryScreen) (Object) this;

        if (selectedTab == CreateSprings.MAIN_TAB.get()) {

            int x = gui.getGuiLeft() + 8;
            int y = gui.getGuiTop() + 17;

            int width = CSpringsGuiTextures.BANNER.getWidth();
            int height = CSpringsGuiTextures.BANNER.getHeight();
            int frameCount = 10;

            int frame = ((int) Minecraft.getInstance().level.getGameTime() / 3) % frameCount;
            float vOffset = (float) (frame * height);

            guiGraphics.blit(
                    CSpringsGuiTextures.BANNER.location,
                    x, y,
                    width, height,
                    0, vOffset,
                    width, height,
                    width, height * frameCount
            );
        }
    }
}
