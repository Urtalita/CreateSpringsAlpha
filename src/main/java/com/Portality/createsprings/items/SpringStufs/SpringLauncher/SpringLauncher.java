package com.Portality.createsprings.items.SpringStufs.SpringLauncher;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.entities.Projectile.SpringAlloyBlockProjectile;
import com.Portality.createsprings.entities.Projectile.SpringProjectile;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.ClientSpringAnimation;
import com.Portality.createsprings.items.SpringStufs.ISpringPoweredTool;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.advanced.Punchcard.ExecutorInfo;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardExecutor;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardInterpritator;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.Portality.createsprings.items.SpringStufs.SpringPoweredCore.*;

public class SpringLauncher extends ProjectileWeaponItem implements CustomArmPoseItem, ISpringPoweredTool {
    private final SpringPoweredCore core;
    public static final float ZOOM_FOV_MODIFIER = 0.1f;
    public static String BlockAmmo = "minecraft:tnt";
    public static String Spyglass = "minecraft:spyglass";

    public static boolean isUsing(ItemStack stack){
        if(stack.has(CSpringsDataComponents.IS_USING)) return stack.get(CSpringsDataComponents.IS_USING);
        return false;
    }

    public SpringLauncher(Properties p_43009_) {
        super(p_43009_);
        Supplier<Item>[] allowedModifficators = new Supplier[]{
                () -> CSpringsItems.PUNCHCARD,
                () -> Items.SPYGLASS,
                Blocks.TNT::asItem
        };

        this.core = new SpringPoweredCore(2, allowedModifficators);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        core.appendHoverText(stack, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);

        ItemStack stack = player.getItemInHand(hand);
        CompoundTag contains = SpringPoweredCore.getContent(stack);

        /*

        stack.set(CSpringsDataComponents.IS_USING, true);

        if(contains.getBoolean(Spyglass)){
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SPYGLASS_USE, SoundSource.PLAYERS,
                    1.0F, 1.0F);
        }
         */

        if(level.isClientSide){
            if(!player.isCreative()){
                ClientSpringAnimation.start();
            }
            return InteractionResultHolder.consume(itemstack);
        }

        int Springs_rn = SpringPoweredCore.getSprings(stack);
        float Stored = getStoredSum(stack);
        stack.set(CSpringsDataComponents.IS_USING, true);
        float power = 1.0F * (Stored / ModConfigs.common().SPRING_CAPACITY.get());

        if (Springs_rn == 2) {
            SpringProjectile projectile = (SpringProjectile) createProjectile(level, stack, player);

            positionProjectileAtMuzzle(projectile, player, 1, 0, -0.3);

            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);

            if(!player.isCreative()){
                stack.set(CSpringsDataComponents.SPRING_AMOUNT, 1);
                putAllStored(new float[]{0f, 0f}, stack);
            }

            level.addFreshEntity(projectile);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS,
                    1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

        } else if(contains.getBoolean(BlockAmmo)){
            SpringAlloyBlockProjectile projectile = (SpringAlloyBlockProjectile) createBlockProjectile(level, stack, player);

            positionProjectileAtMuzzle(projectile, player, 1, 0, -0.3);

            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);

            if (!player.isCreative()) {
                contains.putBoolean(BlockAmmo, false);
                putAllStored(new float[]{0f, 0f}, stack);
            }

            level.addFreshEntity(projectile);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS,
                    1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
        }

        return InteractionResultHolder.consume(itemstack);
    }

    public static void positionProjectileAtMuzzle(AbstractArrow projectile, Player shooter, double forward, double side, double up) {
        double sideOffset = shooter.getMainArm() == HumanoidArm.RIGHT ? side : -side;

        Vec3 look = shooter.getLookAngle();
        Vec3 right = new Vec3(-look.z, 0, look.x).normalize();

        Vec3 spawnPos = shooter.getEyePosition()
                .add(look.scale(forward))
                .add(right.scale(sideOffset))
                .add(0, up, 0);

        projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() == CSpringsBlocks.SPRING.asItem();
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {

    }

    public AbstractArrow createProjectile(Level level, ItemStack stack, LivingEntity shooter) {
        SpringProjectile projectile = new SpringProjectile(level, shooter);
        projectile.setBaseDamage(3.5); // Установка урона
        return projectile;
    }

    public AbstractArrow createBlockProjectile(Level level, ItemStack stack, LivingEntity shooter) {
        SpringAlloyBlockProjectile projectile = new SpringAlloyBlockProjectile(level, shooter);
        projectile.setBaseDamage(3.5);
        return projectile;
    }

    public boolean isSpyglass(LivingEntityUseItemEvent.Start event){
        ItemStack stack = event.getItem();
        CompoundTag tag = SpringPoweredCore.getContent(stack);
        return tag.getBoolean(Spyglass);
    }

    public boolean isSpyglass(ComputeFovModifierEvent event){
        ItemStack stack = event.getPlayer().getUseItem();
        CompoundTag tag = SpringPoweredCore.getContent(stack);
        return tag.getBoolean(Spyglass);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {

    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        CompoundTag contains = SpringPoweredCore.getContent(oldStack);

        if (isUsing(oldStack) && contains.getBoolean(Spyglass)){
            return true;
        }
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return false; // Отключаем сброс анимации ломания блоков
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true; // Отключаем анимацию взмаха рукой
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SpringLauncherRenderer()));
    }

    @Override
    @Nullable
    public HumanoidModel.ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
        if (!player.swinging) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        return null;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return core.getTooltipImage(stack);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess slotaccess) {
        if (core.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, slotaccess)){
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if(core.overrideStackedOnOther(stack, slot, action, player)){
            return true;
        }

        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if(!(entity instanceof Player player)) return;

        if(level.getGameTime() % 10 == 0){
            PunchcardInterpritator.DoPunchcardLogic(new ExecutorInfo(stack, level, player, slotId, isSelected, PunchcardExecutor.SPRING_LAUNCHER, this));
        }
    }

    @Override
    public SpringPoweredCore getCore() {
        return core;
    }
}
