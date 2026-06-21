package com.Portality.createsprings.mixins.banner;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.client.CSpringsGuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public class MixinCreativeModeInventoryScreen {
    @Shadow
    private static CreativeModeTab selectedTab;

    @Unique
    private float csprings$bannerTicks = 0;

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void drawCustomBanner(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        CreativeModeInventoryScreen gui = (CreativeModeInventoryScreen) (Object) this;

        if (selectedTab == CreateSprings.MAIN_TAB.get()) {

            int x = gui.getGuiLeft() + 8;
            int y = gui.getGuiTop() + 17;

            int width = CSpringsGuiTextures.BANNER_0.getWidth();
            int height = CSpringsGuiTextures.BANNER_0.getHeight();
            int frameCount = 10;

            boolean isHovering = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

            if (isHovering) {
                csprings$bannerTicks += partialTick * 0.3f;
            }

            int frame = (int) (csprings$bannerTicks) % frameCount;

            CSpringsGuiTextures currentFrameTexture = getBannerFrameTexture(frame);

            guiGraphics.blit(
                    currentFrameTexture.getLocation(),
                    x, y,
                    width, height,
                    0, 0,
                    width, height,
                    width, height
            );
        }
    }

    @Unique
    private CSpringsGuiTextures getBannerFrameTexture(int frame) {
        return switch (frame) {
            case 0 -> CSpringsGuiTextures.BANNER_0;
            case 1 -> CSpringsGuiTextures.BANNER_1;
            case 2 -> CSpringsGuiTextures.BANNER_2;
            case 3 -> CSpringsGuiTextures.BANNER_3;
            case 4 -> CSpringsGuiTextures.BANNER_4;
            case 5 -> CSpringsGuiTextures.BANNER_5;
            case 6 -> CSpringsGuiTextures.BANNER_6;
            case 7 -> CSpringsGuiTextures.BANNER_7;
            case 8 -> CSpringsGuiTextures.BANNER_8;
            default -> CSpringsGuiTextures.BANNER_9;
        };
    }
}
