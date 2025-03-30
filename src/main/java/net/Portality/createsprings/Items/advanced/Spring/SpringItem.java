package net.Portality.createsprings.Items.advanced.Spring;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringDrill.SpringDrillRenderer;
import net.Portality.createsprings.menus.Spring.SpringMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class SpringItem extends BlockItem {

    private static final SoundEvent CHARGE_SOUND = SoundEvents.NOTE_BLOCK_PLING.get();
    private static final int TimeNeed = 2;

    public SpringItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }


    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        /*
        if (!stack.getOrCreateTag().hasUUID("TargetUUID") && !level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inv, p) -> new SpringMenu(containerId, inv, stack),
                    Component.literal("")
            ));
        }

         */
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }



    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide()) return InteractionResult.PASS;



        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        player.startUsingItem(player.getUsedItemHand());
        return super.useOn(context);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (!(entity instanceof Player player) || level.isClientSide())
            return;

        int time_pass = getUseDuration(stack) - timeCharged;

        // Параметры рейкаста
        double distance = 5;
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 end = start.add(player.getLookAngle().scale(distance));

        // Находим сущность в направлении взгляда
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                level, player, start, end,
                new AABB(start, end),
                e -> !e.isSpectator() && e.isPickable()
        );

        if (entityHit != null) {
            Entity target = entityHit.getEntity();

            if (target instanceof LivingEntity livingTarget) {
                if (time_pass > TimeNeed){
                    float storedSu = GetStoredSu(stack);
                    launchEntity(livingTarget, storedSu, (Player) entity);

                    CompoundTag tag = stack.getOrCreateTag();
                    CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
                    if (!player.isCreative()){
                        blockEntityTag.putFloat("Stored", 0);
                    }
                    tag.put("BlockEntityTag", blockEntityTag);
                }
            }
        } else {
            if (time_pass > TimeNeed){
                float storedSu = GetStoredSu(stack);
                launchPlayer(storedSu, (Player) entity);

                CompoundTag tag = stack.getOrCreateTag();
                CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
                if (!player.isCreative()){
                    blockEntityTag.putFloat("Stored", 0);
                }
                tag.put("BlockEntityTag", blockEntityTag);
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeRemaining) {
        if (entity instanceof Player player) {
            int chargeTime = this.getUseDuration(stack) - timeRemaining;

            if (chargeTime == TimeNeed) {

                spawnParticles(level, entity.getPosition(1).add(entity.getViewVector(1)));
                level.playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        CHARGE_SOUND,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.5F
                );
            } else if (chargeTime > TimeNeed){
                if(entity.onGround()){
                    player.setPos(player.getX(), player.getY()+0.15, player.getZ());
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // Максимальное время удержания
    }

    private void  launchPlayer(float su, Player player) {
        double launchVelocity = su / 16000;
        final Vec3 vec = player.getViewVector(1);

        player.addDeltaMovement(new Vec3(vec.x * -launchVelocity, vec.y * -launchVelocity / 3, vec.z * -launchVelocity));
        player.hurtMarked = true;
    }

    private void  launchEntity(LivingEntity entity, float su, Player player) {
        double launchVelocity = su / 16000;
        final Vec3 vec = player.getViewVector(1);

        entity.addDeltaMovement(new Vec3(vec.x * launchVelocity, vec.y * launchVelocity / 3, vec.z * launchVelocity));
        entity.hurtMarked = true;
    }

    public float GetStoredSu(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        float stored = 0;

        CompoundTag BlockEntityTag = tag.getCompound("BlockEntityTag");
        stored = BlockEntityTag.getFloat("Stored");

        return stored;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    private void spawnParticles(Level level, Vec3 pos) {
        // Генерация частиц вокруг игрока
        for(int i = 0; i < 5; ++i) {
            level.addParticle(ParticleTypes.CLOUD,
                    pos.x + (Math.random() - 0.5),
                    pos.y + 1.0,
                    pos.z + (Math.random() - 0.5),
                    0, 0.1, 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        float stored = GetStoredSu(stack);
        float capacity = CreateSprings.SPRING_CAPACITY;
        tooltip.add(Component.literal("su: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(stored))).withStyle(ChatFormatting.DARK_GRAY)
               .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
               .append(Component.literal(String.valueOf(capacity))).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SpringItemRenderer()));
    }
}
