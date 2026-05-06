package com.Portality.createsprings.compat;

import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import net.minecraft.network.chat.Component;

public class SableCompatHandler {
    public static ForceGroup springPush = new ForceGroup(Component.literal("spring Push"), Component.literal(""), 0x00000, false);

    SableCompatHandler(){

    }

    public static void register(){

    }
}
