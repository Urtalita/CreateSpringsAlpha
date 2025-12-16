package net.Portality.createsprings.menus.PortativeEngine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.Portality.createsprings.client.CSpringsGuiTextures;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class LampButton extends AbstractSimiWidget {
    public CSpringsGuiTextures bg = CSpringsGuiTextures.PORTATIVE_STEAM_BG;

    protected LampButton(int x, int y) {
        super(x, y, 40, 40);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        bg = (bg == CSpringsGuiTextures.PORTATIVE_STEAM_BG) ? CSpringsGuiTextures.PORTATIVE_STEAM_BG_POWERED : CSpringsGuiTextures.PORTATIVE_STEAM_BG;
        runCallback(mouseX, mouseY);
    }
}
