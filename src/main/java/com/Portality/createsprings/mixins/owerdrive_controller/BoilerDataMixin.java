package com.Portality.createsprings.mixins.owerdrive_controller;

import com.Portality.createsprings.client.CSpringsLang;
import com.Portality.createsprings.config.ModConfigs;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.foundation.utility.CreateLang;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BoilerData.class)
public class BoilerDataMixin {
    @Shadow public int activeHeat;

    @Inject(
            method = "addToGoggleTooltip",
            at = @At("HEAD"),
            cancellable = true)

    private void overdriveStats(List<Component> tooltip, boolean isPlayerSneaking, int boilerSize, CallbackInfoReturnable<Boolean> cir){
        final BoilerData self = (BoilerData) (Object) this;
        final BoilerDataAcsessor accessor = (BoilerDataAcsessor) (Object) this;
        if (!self.isActive())
            cir.setReturnValue(false);

        self.calcMinMaxForSize(boilerSize);

        CreateLang.text(" ").add(
                        CSpringsLang.translate("boiler.status")).space()
                .add(CSpringsLang.translate("boiler.owerdrive").style(ChatFormatting.DARK_RED))
                .forGoggles(tooltip);

        CreateLang.builder().add(getOwerdriveSizeComponent(true, false)).forGoggles(tooltip, 1);
        CreateLang.builder().add(getOwerdriveWaterComponent(true, false)).forGoggles(tooltip, 1);
        CreateLang.builder().add(getOverdriveHeatComponent(true, false)).forGoggles(tooltip, 1);

        if (self.attachedEngines == 0)
            cir.setReturnValue(true);

        int boilerLevel = Math.min(self.activeHeat, Math.min(accessor.getMaxHeatForWater(), accessor.getMaxHeatForSize()));
        double totalSU = self.getEngineEfficiency(boilerSize) * 16 * Math.max(boilerLevel, self.attachedEngines)
                * BlockStressValues.getCapacity(AllBlocks.STEAM_ENGINE.get());

        tooltip.add(CommonComponents.EMPTY);

        if (self.attachedEngines > 0 && accessor.getMaxHeatForSize() > 0 && accessor.getMaxHeatForWater() == 0 && (self.passiveHeat ? 1 : activeHeat) > 0) {
            CreateLang.translate("boiler.water_input_rate")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.number(self.waterSupply)
                    .style(ChatFormatting.BLUE)
                    .add(CreateLang.translate("generic.unit.millibuckets"))
                    .add(CreateLang.text(" / ")
                            .style(ChatFormatting.GRAY))
                    .add(CreateLang.translate("boiler.per_tick", CreateLang.number(accessor.getWaterSupplyPerLevel())
                                    .add(CreateLang.translate("generic.unit.millibuckets")))
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
            cir.setReturnValue(true);
        }

        CreateLang.translate("tooltip.capacityProvided")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        CreateLang.number(totalSU)
                .translate("generic.unit.stress")
                .style(ChatFormatting.AQUA)
                .space()
                .add((self.attachedEngines == 1 ? CreateLang.translate("boiler.via_one_engine")
                        : CreateLang.translate("boiler.via_engines", self.attachedEngines)).style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        CreateLang.text(" ").add(
                        CreateLang.number(Math.round(totalSU * 0.4f)).style(ChatFormatting.AQUA))
                .add(CreateLang.translate("generic.unit.stress").style(ChatFormatting.AQUA).space())
                .add(CSpringsLang.translate("boiler.owerdrive_su").style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip);

        cir.setReturnValue(false);
        cir.cancel();
    }

    @Unique
    public MutableComponent getOwerdriveSizeComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting... styles) {
        final BoilerDataAcsessor accessor = (BoilerDataAcsessor) (Object) this;
        return owerdriveComponentHelper("size", "pressure", accessor.getMaxHeatForSize(), forGoggles, useBlocksAsBars, styles);
    }

    @Unique
    public MutableComponent getOwerdriveWaterComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting... styles) {
        final BoilerDataAcsessor accessor = (BoilerDataAcsessor) (Object) this;
        return owerdriveComponentHelper("water", "colorant", accessor.getMaxHeatForWater(), forGoggles, useBlocksAsBars, styles);
    }

    @Unique
    public MutableComponent getOverdriveHeatComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting... styles) {
        final BoilerData self = (BoilerData) (Object) this;
        return owerdriveComponentHelper("heat", "over_heat", self.passiveHeat ? 1 : activeHeat, forGoggles, useBlocksAsBars, styles);
    }

    @Unique
    private MutableComponent owerdriveComponentHelper(String label, String additional, int level, boolean forGoggles, boolean useBlocksAsBars,
                                                      ChatFormatting... styles) {
        final BoilerData self = (BoilerData) (Object) this;
        MutableComponent base = useBlocksAsBars ? owerdriveBlockComponent(level) : owerdriveBarComponent(level);

        if (!forGoggles)
            return base;

        ChatFormatting style1 = styles.length >= 1 ? styles[0] : ChatFormatting.STRIKETHROUGH;
        ChatFormatting style2 = styles.length >= 2 ? styles[1] : ChatFormatting.DARK_GRAY;

        return Component.empty()
                .append(CreateLang.translateDirect("boiler." + label).withStyle(style1, ChatFormatting.GRAY))
                .append(CSpringsLang.translateDirect("boiler." + additional).withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.RED))
                .append(CreateLang.translateDirect("boiler." + label + "_dots").withStyle(style2))
                .append(base);
    }

    @Unique
    private MutableComponent owerdriveBlockComponent(int level) {
        final BoilerDataAcsessor accessor = (BoilerDataAcsessor) (Object) this;
        return Component.literal("" + "\u2588".repeat(accessor.minValue()) + "\u2592".repeat(level - accessor.minValue()) + "\u2591".repeat( accessor.maxValue() - level));
    }

    @Unique
    private MutableComponent owerdriveBarComponent(int level) {
        final BoilerDataAcsessor accessor = (BoilerDataAcsessor) (Object) this;
        int minValue = accessor.minValue();
        int maxValue = accessor.maxValue();
        return Component.empty()
                .append(owerdriveBars(Math.max(0, minValue - 1), ChatFormatting.DARK_GREEN))
                .append(owerdriveBars(minValue > 0 ? 1 : 0, ChatFormatting.GREEN))
                .append(owerdriveBars(Math.max(0, level - minValue), ChatFormatting.DARK_GREEN))
                .append(owerdriveBars(Math.max(0, maxValue - level), ChatFormatting.DARK_RED))
                .append(owerdriveBars(Math.max(0, Math.min(18 - maxValue, ((maxValue / 5 + 1) * 5) - maxValue)),
                        ChatFormatting.DARK_GRAY));

    }

    @Unique
    private MutableComponent owerdriveBars(int level, ChatFormatting format) {
        return Component.literal(Strings.repeat('|', level))
                .withStyle(format);
    }
}
