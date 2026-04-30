package com.Portality.createsprings.mixins.banner;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.client.CSpringsGuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
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

            int width = CSpringsGuiTextures.BANNER.getWidth();
            int height = CSpringsGuiTextures.BANNER.getHeight();
            int frameCount = 10;

            boolean isHovering = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

            if (isHovering) {
                csprings$bannerTicks += partialTick * 0.3f;
            }

            // 3. Вычисляем текущий кадр на основе накопленных тиков
            int frame = (int) (csprings$bannerTicks) % frameCount;

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
