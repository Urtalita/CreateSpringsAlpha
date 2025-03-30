package net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher;

import com.mojang.blaze3d.platform.Window;
import net.Portality.createsprings.CreateSprings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class OverlayHandler {
    private static final ResourceLocation SCOPE_TEXTURE =
            new ResourceLocation(CreateSprings.MODID, "textures/gui/springlauncher.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.SPYGLASS.id())) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player != null && player.isUsingItem()) {
                ItemStack usingItem = player.getUseItem();
                if (usingItem.getItem() instanceof SpringLauncher) {
                    CompoundTag tag = usingItem.getOrCreateTag();
                    CompoundTag contains = tag.getCompound("contains");
                    if (contains.getBoolean(SpringLauncher.Spyglass)){
                        // Отменяем рендер стандартного оверлея
                        event.setCanceled(true);

                        // Рендер кастомного оверлея
                        renderCustomOverlay(minecraft, event.getGuiGraphics());
                    }
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
