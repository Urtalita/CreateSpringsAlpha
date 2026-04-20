package com.Portality.createsprings.items.SpringStufs;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class SpringSpeedClientHandler {
    public static float speed = 0;
    public static float LastSpeed = 0;
    public static float scroll = 0;
    public static float LastScroll = 0;

    public static void updateSpeed(float newLastSpeed, float newSpeed){
        LastSpeed = newLastSpeed;
        speed = newSpeed;
    }

    public static float interpolateSpeed(){
        return Mth.lerp((Minecraft.getInstance().level.getGameTime() + AnimationTickHolder.getPartialTicks() - 1) % 40 / 40f ,LastSpeed / 100, speed / 100);
    }

    public static void onTick(){
        LastScroll = scroll;
        scroll += interpolateSpeed();
    }

    public static float getScroll(){
        return Mth.lerp(AnimationTickHolder.getPartialTicks(), LastScroll, scroll);
    }
}
