package com.Portality.createsprings.items.advanced.Spring;

import com.Portality.createsprings.client.CSpringsLang;
import com.Portality.createsprings.client.sounds.CSpringsSounds;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.entities.Packages.HatPackageEntity;
import com.Portality.createsprings.entities.Packages.SusPackageEntity;
import com.Portality.createsprings.items.BouncyBlockItem;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.ClientSpringAnimation;
import com.Portality.createsprings.items.advanced.SusPackage.SusPackageItem;
import com.Portality.createsprings.items.advanced.hat.HatItem;
import com.simibubi.create.content.equipment.wrench.WrenchItemRenderer;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;

import static com.Portality.createsprings.items.advanced.hat.HatItem.readStackFromNBT;
import static com.Portality.createsprings.items.advanced.hat.HatItem.setPackageColor;
import static com.Portality.createsprings.utill.Helpers.EntityHelper.getOppositeHand;

public class SpringItem extends BouncyBlockItem {
    private static final int TimeNeed = 2;

    public SpringItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_.fireResistant());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        InteractionHand hand2 = getOppositeHand(player.getUsedItemHand());
        if(player.getItemInHand(hand2).getItem() instanceof PackageItem){
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.pass(stack);
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
        if (!(entity instanceof Player player))
            return;

        int time_pass = getUseDuration(stack, entity) - timeCharged;
        if(time_pass > TimeNeed){
            if(level.isClientSide()){
                ClientSpringAnimation.start();
            } else {
                CSpringsSounds.playBweum(level, player.getOnPos());
                if(LaunchItemInHand(player, level)){return;}

                LaunchPlayerOrEntity(player, level, stack, entity);
            }
        }
    }

    private boolean LaunchItemInHand(Player player, Level level){
        InteractionHand hand = getOppositeHand(player.getUsedItemHand());
        if(player.getItemInHand(hand).getItem() instanceof PackageItem){

            if(player.getItemInHand(hand).getItem() instanceof SusPackageItem){
                launchPackage(player.getItemInHand(hand), level, player, player.getItemInHand(player.getUsedItemHand()), true, false);
                return true;
            }

            launchPackage(player.getItemInHand(hand), level, player, player.getItemInHand(player.getUsedItemHand()), false, false);
            return true;
        } else {
            if(player.getItemInHand(hand).getItem() instanceof HatItem){
                launchPackage(player.getItemInHand(hand), level, player, player.getItemInHand(player.getUsedItemHand()), false, true);
                return true;
            }
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SpringItemRenderer()));
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
            float storedSu = getStoredSu(stack);
            launchEntity(target, storedSu, (Player) entity);
            SetZeroSu(stack, player);
        } else {
            float storedSu = getStoredSu(stack);
            launchPlayer(storedSu, (Player) entity);
            SetZeroSu(stack, player);
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeRemaining) {
        if (entity instanceof Player player) {
            int chargeTime = this.getUseDuration(stack, entity) - timeRemaining;

            if (chargeTime == TimeNeed) {

                spawnParticles(level, entity.getPosition(1).add(entity.getViewVector(1)));

                level.playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        CSpringsSounds.BWEUM.get(),
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                );
            } else if (chargeTime > TimeNeed){
                if(entity.onGround()){
                    player.setPos(player.getX(), player.getY()+0.15, player.getZ());
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000; // Максимальное время удержания
    }

    private void  launchPlayer(float su, Player player) {
        double launchVelocity = su / 16000 * ModConfigs.common().KNOCKBACK_COEF.get() / 4f;
        final Vec3 vec = player.getViewVector(1);

        player.addDeltaMovement(new Vec3(vec.x * -launchVelocity, vec.y * -launchVelocity / 3, vec.z * -launchVelocity));
        player.hurtMarked = true;
    }

    private void  launchEntity(Entity entity, float su, Player player) {
        double launchVelocity = su / 16000 * ModConfigs.common().KNOCKBACK_COEF.get() / 4f;
        final Vec3 vec = player.getViewVector(1);

        entity.addDeltaMovement(new Vec3(vec.x * launchVelocity, vec.y * launchVelocity / 3, vec.z * launchVelocity));
        entity.hurtMarked = true;
    }

    public void launchPackage(ItemStack stack, Level world, Player player, ItemStack springStack, boolean isSusPackage, boolean isHat) {
        float strength = ModConfigs.common().SPRING_CAPACITY.get() / getStoredSu(springStack);

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
            packageEntity.setBox(copy);
        } else if(isHat){

            HatPackageEntity HpackageEntity = new HatPackageEntity(world, vec.x, vec.y, vec.z);
            HpackageEntity.setContains(readStackFromNBT(stack));
            packageEntity = HpackageEntity;
            packageEntity.setBox(setPackageColor(new ItemStack(CSpringsItems.HITBOX_HAT.get()), stack));

        } else {
            packageEntity = new PackageEntity(world, vec.x, vec.y, vec.z);
            packageEntity.setBox(copy);
        }
        //
        packageEntity.setDeltaMovement(motion);
        packageEntity.tossedBy = new WeakReference<>(player);
        world.addFreshEntity(packageEntity);
    }

    public static float getPackageVelocity(float strength) {
        return (float) (strength * ModConfigs.common().KNOCKBACK_COEF.get());
    }

    private void SetZeroSu(ItemStack stack, Player player){
        if (!player.isCreative()){
            SetSu(stack, 0);
        }
    }

    public static void SetSu(ItemStack stack, float su){
        if(stack.has(DataComponents.BLOCK_ENTITY_DATA)){
            stack.set(DataComponents.BLOCK_ENTITY_DATA,
                    CustomData.EMPTY.update(tag -> {
                                tag.putFloat("Stored", su);
                                tag.putLong("Id", -99999999999999L);
                                tag.putString("id", "createsprings:spring");
                            }
                    ));
        }
    }

    public static float getStoredSu(ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);

        if (data != null) {
            return data.copyTag().getFloat("Stored");
        }

        return 0f;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    private void spawnParticles(Level level, Vec3 pos) {
        for(int i = 0; i < 5; ++i) {
            level.addParticle(ParticleTypes.CLOUD,
                    pos.x + (Math.random() - 0.5),
                    pos.y + 1.0,
                    pos.z + (Math.random() - 0.5),
                    0, 0.1, 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        float stored = getStoredSu(stack);
        float capacity = ModConfigs.common().SPRING_CAPACITY.get();
        tooltip.add(CreateLang.text(" ").add(
                CSpringsLang.transformTime(stored)
                ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                .add(CSpringsLang.transformTime(capacity))).component());
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
