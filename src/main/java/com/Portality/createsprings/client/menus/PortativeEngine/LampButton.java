package com.Portality.createsprings.client.menus.PortativeEngine;

import com.Portality.createsprings.client.CSpringsGuiTextures;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;

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
