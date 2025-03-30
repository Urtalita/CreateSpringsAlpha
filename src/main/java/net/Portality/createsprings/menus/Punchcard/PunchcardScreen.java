package net.Portality.createsprings.menus.Punchcard;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.Portality.createsprings.CreateSprings;

public class PunchcardScreen extends AbstractContainerScreen<PunchcardMenu> {
    protected IconButton onButton;
    protected Indicator facingIndicator;
    protected IconButton offButton;

    private IconButton confirmButton;
    private EditBox textInput;

    public static String savedText = "";
    private static final ResourceLocation BACKGROUND = new ResourceLocation(CreateSprings.MODID, "textures/gui/demo_background.png");

    public PunchcardScreen(PunchcardMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);

        guiGraphics.drawString(font, menu.getBlockName(), leftPos + 90, topPos + 101, 0xFFFFFF);
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
        if (!menu.stack.isEmpty()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(this.leftPos + 54 - 8, this.topPos + 105 - 8, 300);
            guiGraphics.pose().scale(2.0F, 2.0F, 1.0F);
            guiGraphics.renderItem(menu.block, 0, 0);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    protected void init() {
        savedText = menu.getPunchcardName();
        super.init();
        this.textInput = new EditBox(
                this.font,
                this.leftPos + 50, this.topPos + 166,
                110, 20,
                Component.literal(savedText)
        );
        this.textInput.setBordered(false);
        this.textInput.setValue(savedText);
        this.textInput.setResponder(text -> savedText = text);
        this.textInput.setTextColor(0xFFFFFF);
        this.textInput.setMaxLength(100);
        this.addRenderableWidget(textInput);

        this.confirmButton = new IconButton(this.leftPos + 193, this.topPos + 161, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> {
            Player player = Minecraft.getInstance().player;

            if (player != null) {
                player.getMainHandItem().setHoverName(Component.literal(savedText));
                player.resetAttackStrengthTicker();
            }
            player.closeContainer();
        });
        this.addRenderableWidget(this.confirmButton);

        this.onButton = new IconButton(this.leftPos + 193, this.topPos + 116, AllIcons.I_CONFIRM);
        this.onButton.withCallback(() -> {

        });
        this.addRenderableWidget(this.onButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Передаем события ввода в текстовое поле
        if (this.textInput.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.textInput.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }


}
