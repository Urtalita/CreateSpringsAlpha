package net.Portality.createsprings.Items.SpringStufs;

import net.Portality.createsprings.config.ModConfigs;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;

import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity.springAnimation;

public class ClientSpringAnimation {
    public static int phase = 0;

    public static void start(){
        phase = ModConfigs.common().SPRING_SPLASH_DURATION.get();
    }

    public static void onTick(){
        if(phase > 0){
            phase -= 1;
        }
    }

    public static float getAnimation(){
        int p = ModConfigs.common().SPRING_SPLASH_DURATION.get() - phase;
        float nextprogress = springAnimation(p);
        float prevProgress = springAnimation(p-1);
        return Mth.lerp(AnimationTickHolder.getPartialTicks(), prevProgress, nextprogress);
    }

    public static boolean isActive(){
        return phase != 0;
    }
}
