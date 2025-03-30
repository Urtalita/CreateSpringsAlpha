package net.Portality.createsprings.blocks.advanced.Spring;

import com.google.common.collect.Lists;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.Portality.createsprings.CreateSprings;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.Arrays;
import java.util.List;

public class SpringBlockEntity extends GeneratingKineticBlockEntity implements IHaveGoggleInformation {

    public float capacity = CreateSprings.SPRING_CAPACITY;
    public float stored = 0;
    private float progress;
    private boolean isGenerating;

    public SpringBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.number(stored).forGoggles(tooltip);
        return true;
    }

    @Override
    public float getSpeed() {
        return super.getSpeed();
    }

    @Override
    public float calculateStressApplied() {
        if (stored < capacity && !isGenerating) {
            return 2.0f;
        } else if (isGenerating) {
            return -128f;
        }
        return 0f;
    }

    @Override
    public void tick() {
        super.tick(); // Важно для базовой логики

        // Режим генерации при активации редстоуном
        if (isGenerating && stored > 0) {
            stored = Math.max(stored - 256, 0);
            updateGeneratedRotation();
        }
        // Режим накопления, если не активировано
        else if (!isGenerating) {
            float CurSpeed = Math.abs(getSpeed());
            stored = Mth.clamp(stored + CurSpeed, 0, capacity);
        }

        progress = stored / capacity;
        notifyUpdate();
    }

    // Сохранение данных
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Generating", isGenerating);
        tag.putFloat("Stored", stored);
    }

    // Загрузка данных
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        isGenerating = tag.getBoolean("Generating");
        stored = tag.getFloat("Stored");
    }

    public float getProgress() {
        return this.progress;
    }

    @Override
    public float getGeneratedSpeed() {
        return isGenerating && stored > 0 ? 16.0f : 0.0f;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
    }

    public void setGenerating(boolean generating) {
        isGenerating = generating;
        updateGeneratedRotation(); // Обновляем физику
        sendData(); // Синхронизация
    }
}