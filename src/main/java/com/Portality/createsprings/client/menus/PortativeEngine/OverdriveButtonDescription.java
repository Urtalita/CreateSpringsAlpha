package com.Portality.createsprings.client.menus.PortativeEngine;

import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class OverdriveButtonDescription extends AbstractSimiWidget {
    protected OverdriveButtonDescription(int x, int y, int w, int h) {
        super(x, y, w, h);

        toolTip.add(Component.translatable("createsprings.pse.tooltip.overdrive_button").withStyle(ChatFormatting.RED));
    }
}
