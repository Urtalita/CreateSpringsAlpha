package com.Portality.createsprings.blocks.advanced.spring;

import com.Portality.createsprings.blocks.advanced.kinetic_interface.IConnectableToPSKI;
import com.Portality.createsprings.config.ModConfigs;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Explosion;

import java.util.ArrayList;
import java.util.List;

public interface ISpringBE extends IConnectableToPSKI {
    void onBlockExploded(BlockPos pos, Explosion explosion);
    GeneratingKineticBlockEntity getBlockEntity();
    void setHardness(double hardness);
    float calcStress();
    boolean isGenerating();

    default String formatter(Integer integer) {
        if(Math.abs(integer) <= 1) return "AUTO";
        return Integer.toString(Math.abs(integer) - 1);
    }

    default void updateHardnessSafe(float newHardness) {
        newHardness = Math.max(0F, newHardness);
        GeneratingKineticBlockEntity gbe = getBlockEntity();

        if (!Mth.equal(this.getHardness(), newHardness)) {
            this.setHardness(newHardness);
            if (gbe.getOrCreateNetwork() != null) {
                gbe.getOrCreateNetwork().remove(gbe);
                gbe.getOrCreateNetwork().add(gbe);
                gbe.getOrCreateNetwork().updateNetwork();
                gbe.sendData();
            }
        }
    }

    default void calculateIdealHardness(boolean isGenerating) {
        GeneratingKineticBlockEntity gbe = getBlockEntity();
        KineticNetwork network = gbe.getOrCreateNetwork();
        if (network == null) return;

        float networkCapacity = network.calculateCapacity();
        float networkStress = network.calculateStress();

        float speed = Math.abs(gbe.getTheoreticalSpeed());
        if (Mth.equal(speed, 0)) {
            updateHardnessSafe(0);
            return;
        }

        float myCurrentStressContribution = calcStress() * speed;

        if (isGenerating) {
            float netLeftInNetwork = (networkCapacity - Math.abs(myCurrentStressContribution)) - networkStress;
            float neededCapacity = -netLeftInNetwork;

            if (neededCapacity <= 0) {
                updateHardnessSafe(0);
            } else {
                if(getStored() <= 0) return;
                float idealHardness = neededCapacity / speed / getImpactCof();

                updateHardnessSafe(idealHardness);
            }
        } else {
            float netLeftInNetwork = networkCapacity - (networkStress - myCurrentStressContribution);

            if (netLeftInNetwork <= 0) {
                updateHardnessSafe(0);

            } else {
                if(getCapacity() - getStored() < 1) {
                    updateHardnessSafe(0);
                    return;
                }
                float idealHardness = netLeftInNetwork / speed / getImpactCof();
                updateHardnessSafe(idealHardness);
            }
        }
    }


    default void addRemainingTime(List<Component> tooltip, boolean isPlayerSneaking, boolean isGenerating, double progress, double prevProgress) {
        GeneratingKineticBlockEntity gbe = getBlockEntity();
        if (Mth.equal(gbe.getSpeed(), 0)) return;

        double rate = Math.abs((progress - prevProgress) * getCapacity());
        double left = isGenerating ? getStored() : getCapacity() - getStored();
        double ticks = left / rate;

        int totalSeconds = (int) Math.floor(ticks / 20);

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (totalSeconds != 0) {
            MutableComponent remainingTime = Component.literal("");

            if (hours != 0) remainingTime.append(Component.literal(hours + "h ").withStyle(ChatFormatting.AQUA));
            if (minutes != 0) remainingTime.append(Component.literal(minutes + "m ").withStyle(ChatFormatting.AQUA));
            remainingTime.append(Component.literal(seconds + "s ").withStyle(ChatFormatting.AQUA));

            CreateLang.text(" ").add(
                            CreateLang.translate("createsprings.time_left").style(ChatFormatting.DARK_GRAY).add(remainingTime))
                    .forGoggles(tooltip);
        }
    }

    default void addChargeInSplashMode(List<Component> tooltip, boolean isPlayerSneaking, double progress) {
        int bars = 20;
        int active = (int) Math.round(bars * progress);
        int left = bars - active;

        if(progress < 0) return;
        if(left < 0) return;
        if(active < 0) return;

        LangBuilder builder = CreateLang.builder();

        builder.add(Component.literal("|".repeat(active)).withStyle(ChatFormatting.AQUA));
        if(progress > 0) builder.add (Component.literal("|").withStyle(ChatFormatting.BLUE));
        builder.add(Component.literal("|".repeat(left)).withStyle(ChatFormatting.GRAY));

        int percent = (int) Math.round(progress * 100);

        CreateLang.translate("spring.saved").style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.text(" ").add(builder).space().add(CreateLang.number(percent)).style(ChatFormatting.AQUA).add(Component.literal("%").withStyle(ChatFormatting.BLUE))
                .forGoggles(tooltip);
    }

     static float springAnimation(int phase) {
        if (phase == 0) {return 1.0f;}
        if (phase == ModConfigs.common().SPRING_SPLASH_DURATION.get()){return 0f;}

        float decay = (float) Math.exp(-0.15 * phase);

        float frequency = (float) (Math.PI * 0.4);

        float oscillation = (float) Math.cos((frequency * phase + Math.PI)/2);

        return decay * oscillation * 2f;
    }
}
