package com.Portality.createsprings.blocks.advanced.spring;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class SpringValueBehavior extends ScrollValueBehaviour {
    public SpringValueBehavior(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(Component.literal("\u27f3")
                        .withStyle(ChatFormatting.BOLD),
                Component.literal("\u27f2")
                        .withStyle(ChatFormatting.BOLD));

        return new ValueSettingsBoard(label, max, 32, rows ,new ValueSettingsFormatter(this::format));
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        int value = Math.max(1, valueSetting.value());
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        setValue(valueSetting.row() == 0 ? -value : value);
    }

    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(value < 0 ? 0 : 1, Math.abs(value));
    }

    private MutableComponent format(ValueSettings settings) {
        if(settings.value() <= 1) return Component.literal("AUTO");
        return CreateLang.number(Math.max(1, Math.abs(settings.value())) - 1)
                .add(CreateLang.text(settings.row() == 0 ? "\u27f3" : "\u27f2")
                        .style(ChatFormatting.BOLD))
                .component();
    }
}

