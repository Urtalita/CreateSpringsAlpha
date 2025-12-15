package net.Portality.createsprings.menus.PortativeEngine;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.server.NetworkHandler;
import net.Portality.createsprings.server.PortativeSteamEngineUpdatePacket;
import net.Portality.createsprings.client.CSpringsGuiTextures;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

import static net.minecraft.ChatFormatting.*;
import static net.minecraft.world.level.material.Fluids.WATER;

public class PortativeEngineScreen extends AbstractSimiContainerScreen<PortativeSteamEngineMenu> {
    private CSpringsGuiTextures background;
    private final CSpringsGuiTextures side = CSpringsGuiTextures.PORTATIVE_STEAM_SIDE;
    private final CSpringsGuiTextures right = CSpringsGuiTextures.PORTATIVE_STEAM_SIDE_RIGHT;
    private final CSpringsGuiTextures boost = CSpringsGuiTextures.PORTATIVE_STEAM_BOOST;
    private AnalogLeverSelector selector;
    private ItemStack stack;
    private LampButton button;
    private RandomSource source = RandomSource.create();
    private int boosted = 0;

    private TooltipDescription leverDescription;
    private TooltipDescription overdriveDescription;
    private TooltipDescription waterDescription;
    private OverdriveButtonDescription overdriveButtonDescription;

    public PortativeEngineScreen(PortativeSteamEngineMenu menu, Inventory inventory, Component title){
        super(menu, inventory, title);
        this.stack = menu.contentHolder;

        if(stack.getOrCreateTag().getBoolean("boost")){
            background = CSpringsGuiTextures.PORTATIVE_STEAM_BG_POWERED;
        } else {
            background = CSpringsGuiTextures.PORTATIVE_STEAM_BG;
        }
    }

    private void renderFluid(GuiGraphics graphics, float filled, int sx, int sy){
        int x = leftPos - 49 + sx;
        int y = topPos + 53 + sy;
        float scaleFactor = 160;

        graphics.pose().pushPose();
        graphics.pose().scale(scaleFactor, scaleFactor, scaleFactor);

        float scaledX = x / scaleFactor;
        float scaledY = y / scaleFactor;

        FluidStack fluidStack = new FluidStack(WATER, (int) (1000));
        ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
                fluidStack, scaledX, scaledY + 140 / scaleFactor - 140 / scaleFactor * filled, 0, scaledX + 40 / scaleFactor, scaledY + 140 / scaleFactor, 0, graphics.bufferSource(), graphics.pose(), LightTexture.FULL_BRIGHT, false, true
        );

        graphics.pose().popPose();
    }

    private void renderBoost(GuiGraphics graphics, float boosted, int sx, int sy){
        int x = leftPos + 253 + sx;
        int y = topPos + 41 + sy;

        graphics.blit(boost.location, x, y, 0, 0, boost.getWidth(), (int) (boost.getHeight() - 109 - 130 * boosted));

        graphics.drawString(font, Component.literal(String.valueOf( (int)(boosted * 100) + "%")), x + 10 + 2 + 10 + 2, y + 20 + 2 + 110, 0x00000000, false);
    }

    private void onSelected(Integer state){
        if(state != 0){
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.BLAZE_SHOOT,0.075F + Minecraft.getInstance().level.random.nextFloat() * 0.075F , 1 + 0.5f * state)
            );
        }
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        super.init();

        int x = leftPos;
        int y = topPos;
        button = new LampButton(213 + x, 163 + y);

        leverDescription = new TooltipDescription(x + 38, y + 68, 180, 100, this::updateMainDescTooltip);
        overdriveDescription = new TooltipDescription(x + 255, y + 41, 62, 164, this::updateOverdriveDescTooltip);
        waterDescription = new TooltipDescription(x - 61, y + 41, 62, 164, this::updateWaterDescTooltip);

        overdriveButtonDescription = new OverdriveButtonDescription(x + 213, y + 163, 40, 40);

        selector = new AnalogLeverSelector(x + 38, y + 68, 90, 6,
                stack.getOrCreateTag().getFloat("targetSpeed") / 15, false);

        addRenderableWidget(selector).withCallback(this::onSelected);
        addRenderableWidget(button);

        addRenderableWidget(leverDescription);
        addRenderableWidget(overdriveDescription);
        addRenderableWidget(waterDescription);
        addRenderableWidget(overdriveButtonDescription);

        boosted = stack.getOrCreateTag().getInt("boosted");
        if(stack.getOrCreateTag().getBoolean("boost")) {
            button.bg = CSpringsGuiTextures.PORTATIVE_STEAM_BG_POWERED;
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if(Minecraft.getInstance().level.getGameTime() % 5 == 0){
            if(button.bg == CSpringsGuiTextures.PORTATIVE_STEAM_BG_POWERED){
                boosted++;
            } else {
                if(boosted > 0){
                    boosted --;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        background = button.bg;

        int sx = getShift();
        int sy = getShift();

        int x = leftPos + sx;
        int y = topPos + sy;

        background.render(graphics, x, y);
        side.render(graphics, x - 61, y + 41);
        renderFluid(graphics, stack.getOrCreateTag().getFloat("water") / 1000f, sx, sy);

        right.render(graphics, x + 255, y + 41);
        renderBoost(graphics, boosted / 100f, sx, sy);

        selector.render(graphics, mouseX, mouseY, partialTicks, sx, sy);

        int invX = (background.getWidth() - AllGuiTextures.PLAYER_INVENTORY.getWidth()) / 2 + x - sx;
        int invY = background.getHeight() + y - 51 - sy;

        leverDescription.render(graphics, mouseX, mouseY, partialTicks);
        overdriveDescription.render(graphics, mouseX, mouseY, partialTicks);
        waterDescription.render(graphics, mouseX, mouseY, partialTicks);
        overdriveButtonDescription.render(graphics, mouseX, mouseY, partialTicks);

        this.renderPlayerInventory(graphics, invX, invY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

    }

    private int getShift(){
        if(button.bg == CSpringsGuiTextures.PORTATIVE_STEAM_BG_POWERED){
            int shift = (boosted / 20) + 1;
            return source.nextInt(-shift, shift);
        }
        return 0;
    }

    @Override
    public void onClose() {
        sendPacket();
        super.onClose();
    }

    private void sendPacket(){
        if(stack.getOrCreateTag().getFloat("engineSpeed") > selector.getState() * 15){
            stack.getOrCreateTag().putFloat("engineSpeed", selector.getState() * 15);
        }
        stack.getOrCreateTag().putFloat("targetSpeed", selector.getState() * 15);
        stack.getOrCreateTag().putFloat("mode", selector.getState() * 15);
        stack.getOrCreateTag().putBoolean("boost", background == CSpringsGuiTextures.PORTATIVE_STEAM_BG_POWERED);

        stack.getOrCreateTag().putInt("boosted", boosted);
        NetworkHandler.CHANNEL.sendToServer(new PortativeSteamEngineUpdatePacket(stack.getOrCreateTag()));
    }

    private ArrayList<Component> updateMainDescTooltip(){
        ArrayList<Component> toolTip = new ArrayList<>();

        int mode = selector.getState();

        toolTip.add(Component.translatable(CreateSprings.MODID + ".pse." + "tooltip." + "cur_mode").append(Component.literal(" " + mode)));
        toolTip.add(Component.empty());

        for(int i = 1; i < 6; i++){
            MutableComponent line = Component.literal("- ");
            line.append(Component.translatable(CreateSprings.MODID + ".pse." + "tooltip." + i));

            if(i == 3){continue;}

            if(i > mode){
                line.withStyle(DARK_GRAY);
            } else {
                line.withStyle(GRAY);
            }
            toolTip.add(line);
        }

        return toolTip;
    }

    private ArrayList<Component> updateOverdriveDescTooltip(){
        ArrayList<Component> tooltip = new ArrayList<>();
        tooltip.addAll(TooltipDescription.splitAndFormat(Component.translatable("createsprings.pse.tooltip.overdrive")));
        tooltip.add(Component.empty());
        tooltip.addAll(TooltipDescription.splitAndFormat(Component.translatable("createsprings.pse.tooltip.overdrive2")));
        return tooltip;
    }

    private ArrayList<Component> updateWaterDescTooltip(){
        ArrayList<Component> tooltip = new ArrayList<>();
        tooltip.addAll(TooltipDescription.splitAndFormat(Component.translatable("createsprings.pse.tooltip.water")));
        return tooltip;
    }
}
