package net.Portality.createsprings.menus.Spring;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SpringScreen extends AbstractContainerScreen<SpringMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(CreateSprings.MODID, "textures/gui/spring_gui.png");

    public SpringScreen(SpringMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int capacity = (int) CreateSprings.SPRING_CAPACITY;

        guiGraphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);

        guiGraphics.drawString(font, menu.GetStoredSu(menu.stack) + " / " + capacity, leftPos + 90, topPos + 125, 0xFFFFFF);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // пусто
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Отрисовка иконки предмета с масштабированием
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.leftPos + 47+2, this.topPos + 114+2, 300);
        guiGraphics.pose().scale(1.6F, 1.6F, 1.8F);
        guiGraphics.renderItem(new ItemStack(ModBlocks.SPRING.get()), 0, 0);
        guiGraphics.pose().popPose();
    }
}
