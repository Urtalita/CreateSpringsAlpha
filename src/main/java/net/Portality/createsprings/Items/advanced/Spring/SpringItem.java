package net.Portality.createsprings.Items.advanced.Spring;

import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.Packages.SusPackageEntity;
import net.Portality.createsprings.Items.advanced.SusPackage.SusPackageItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;

import static net.Portality.createsprings.utill.Helpers.EntityHelper.getOppositeHand;

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
        /* if (!stack.getOrCreateTag().hasUUID("TargetUUID") && !level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inv, p) -> new SpringMenu(containerId, inv, stack),
                    Component.literal("")
            ));
        } */


        InteractionHand hand2 = getOppositeHand(player.getUsedItemHand());
        if(player.getItemInHand(hand2).getItem() instanceof PackageItem){
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

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
        if(time_pass > TimeNeed){
            if(LaunchItemInHand(player, level)){return;}

            LaunchPlayerOrEntity(player, level, stack, entity);
        }
    }

    private boolean LaunchItemInHand(Player player, Level level){
        InteractionHand hand = getOppositeHand(player.getUsedItemHand());
        if(player.getItemInHand(hand).getItem() instanceof PackageItem){
            if(player.getItemInHand(hand).getItem() instanceof SusPackageItem){
                launchPackage(player.getItemInHand(hand), level, player, player.getItemInHand(player.getUsedItemHand()), true);
                return true;
            }
            launchPackage(player.getItemInHand(hand), level, player, player.getItemInHand(player.getUsedItemHand()), false);
            return true;
        }
        return false;
    }

    private void LaunchPlayerOrEntity(Player player, Level level, ItemStack stack, LivingEntity entity){
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
                float storedSu = getStoredSu(stack);
                launchEntity(livingTarget, storedSu, (Player) entity);
                SetZeroSu(stack, player);
            }
        } else {
            float storedSu = getStoredSu(stack);
            launchPlayer(storedSu, (Player) entity);
            SetZeroSu(stack, player);
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

    public void launchPackage(ItemStack stack, Level world, Player player, ItemStack springStack, boolean isSusPackage) {
        float strength = CreateSprings.SPRING_CAPACITY / getStoredSu(springStack);

        SetZeroSu(springStack, player);

        float f = getPackageVelocity(strength);
        if (f < 0.1D)
            return;
        if (world.isClientSide)
            return;

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW,
                SoundSource.NEUTRAL, 0.5F, 0.5F);

        ItemStack copy = stack.copy();
        if (!player.getAbilities().instabuild)
            stack.shrink(1);

        Vec3 vec = new Vec3(player.getX(), player.getY() + player.getBoundingBox()
                .getYsize() / 2f, player.getZ());
        Vec3 motion = player.getLookAngle()
                .scale(f * 2);
        vec = vec.add(motion);

        PackageEntity packageEntity;
        if(isSusPackage){
            SusPackageEntity SpackageEntity = new SusPackageEntity(world, vec.x, vec.y, vec.z);
            SpackageEntity.power = getStoredSu(springStack);
            packageEntity = SpackageEntity;
        } else {
            packageEntity = new PackageEntity(world, vec.x, vec.y, vec.z);
        }
        packageEntity.setBox(copy);
        packageEntity.setDeltaMovement(motion);
        packageEntity.tossedBy = new WeakReference<>(player);
        world.addFreshEntity(packageEntity);
    }

    public static float getPackageVelocity(float strength) {
        return strength * 4;
    }

    private void SetZeroSu(ItemStack stack, Player player){
        if (!player.isCreative()){
            SetSu(stack, 0);
        }
    }

    private void SetSu(ItemStack stack, float su){
        CompoundTag tag = stack.getOrCreateTag();

        CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
        blockEntityTag.putFloat("Stored", su);
        tag.put("BlockEntityTag", blockEntityTag);
    }

    public static float getStoredSu(ItemStack stack){
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
        float stored = getStoredSu(stack);
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
