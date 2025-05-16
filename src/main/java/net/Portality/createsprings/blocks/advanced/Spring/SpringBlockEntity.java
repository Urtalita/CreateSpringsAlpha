package net.Portality.createsprings.blocks.advanced.Spring;

import com.google.common.collect.Lists;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.Portality.createsprings.CreateSprings;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.Arrays;
import java.util.List;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class SpringBlockEntity extends GeneratingKineticBlockEntity implements IHaveGoggleInformation {

    public float capacity = CreateSprings.SPRING_CAPACITY;
    public float stored = 0;
    private float progress;
    private float prevProgress;
    private boolean isGenerating;
    public boolean splashMode;
    private int phase = 0;

    public SpringBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean ret = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        CreateLang.translate("spring.saved").style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.text(" ").add(
                CreateLang.number(stored).style(ChatFormatting.AQUA).space()
                ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                        .add(CreateLang.number(CreateSprings.SPRING_CAPACITY).style(ChatFormatting.AQUA).space()
                                .add(CreateLang.translate("spring.su").style(ChatFormatting.DARK_GRAY))))
                .forGoggles(tooltip);
        return ret;
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
            if(stored > 128){
                return -128f;
            }
        }
        return 0f;
    }

    @Override
    public void tick() {
        super.tick(); // Важно для базовой логики
        if(isGenerating && splashMode && stored != 0){
            prevProgress = progress;
            progress = springAnimation(phase) * (stored / CreateSprings.SPRING_CAPACITY);

            if(phase == 1){
                launchEntitiesInFront();
            }

            phase++;
            if(phase == 40){
                phase = 0;
                stored = 0;
                isGenerating = false;
                updateGeneratedRotation();
                notifyUpdate();
            }
            return;
        }

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
        prevProgress = progress;
    }

    public static float springAnimation(int phase) {
        if (phase == 0) {
            return 1.0f;
        }
        float decay = (float) Math.exp(-0.15 * phase);

        float frequency = (float) (Math.PI * 0.4);

        float oscillation = (float) Math.cos((frequency * phase + Math.PI)/2);

        return decay * oscillation * 2f;
    }

    // Сохранение данныхt
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Generating", isGenerating);
        tag.putFloat("Stored", stored);
        tag.putInt("phase", phase);
        tag.putBoolean("splashMode", splashMode);
    }

    // Загрузка данных
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        isGenerating = tag.getBoolean("Generating");
        stored = tag.getFloat("Stored");
        phase = tag.getInt("phase");
        splashMode = tag.getBoolean("splashMode");
    }

    public float getProgress(float pt) {return Mth.lerp(pt, prevProgress, progress);}

    @Override
    public float getGeneratedSpeed() {
        return isGenerating && !splashMode && stored > 0 ? 16.0f : 0.0f;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
    }

    public void setGenerating(boolean generating) {
        if(phase > 0){return;}
        phase = 0;
        isGenerating = generating;

        updateGeneratedRotation(); // Обновляем физику
        sendData(); // Синхронизация
    }

    public void launchEntitiesInFront() {
        if (level == null || level.isClientSide) return;

        Direction facing = getBlockState().getValue(FACING).getOpposite();
        BlockPos targetPos = worldPosition.relative(facing);

        // Ищем все ентити в соседнем блоке
        AABB searchArea = new AABB(targetPos);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, searchArea);

        for (Entity entity : entities) {
            Vec3 direction = new Vec3(
                    facing.getStepX(),
                    facing.getStepY(),
                    facing.getStepZ()
            ).scale(1.0);

            entity.setDeltaMovement(direction.scale(3).scale(stored / CreateSprings.SPRING_CAPACITY));
            entity.hurtMarked = true;
        }
    }
}