package com.Portality.createsprings.items.SpringStufs.SpringLauncher;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@OnlyIn(Dist.CLIENT)
public class OverlayHandler {
    private static final ResourceLocation SCOPE_TEXTURE =
             ResourceLocation.fromNamespaceAndPath(CreateSprings.MODID, "textures/gui/springlauncher.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player != null && player.isUsingItem()) {
            ItemStack usingItem = player.getUseItem();

            if (usingItem.getItem() instanceof SpringLauncher) {
                CompoundTag contains = SpringPoweredCore.getContent(usingItem);
                if (contains.getBoolean(SpringLauncher.Spyglass)){
                    // Отменяем рендер стандартного оверлея
                    event.setCanceled(true);

                    // Рендер кастомного оверлея
                    renderCustomOverlay(minecraft, event.getGuiGraphics());
                }
            }
        }
    }

    private static void renderCustomOverlay(Minecraft mc, GuiGraphics guiGraphics) {
        Window window = mc.getWindow();
        int height = window.getGuiScaledHeight();
        int width = window.getGuiScaledWidth();

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(
                SCOPE_TEXTURE,
                0, 0,
                width, height,
                0.0F, 0.0F,
                16, 16,
                16, 16
        );
    }
}
