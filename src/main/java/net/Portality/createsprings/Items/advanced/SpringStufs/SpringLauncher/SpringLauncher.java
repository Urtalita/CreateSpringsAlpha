package net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher;

import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.Projectile.SpringAlloyBlockProjectile;
import net.Portality.createsprings.Entities.Projectile.SpringProjectile;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SpringLauncher extends ProjectileWeaponItem implements CustomArmPoseItem {
    private final SpringPoweredCore core;
    public static final float ZOOM_FOV_MODIFIER = 0.1f;
    public static String BlockAmmo = CreateSprings.MODID + ":spring_alloy_block";
    public static String Spyglass = "minecraft:spyglass";

    public SpringLauncher(Properties p_43009_) {
        super(p_43009_);
        this.core = new SpringPoweredCore(2);;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        core.appendHoverText(stack, level, components, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);

        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag contains = tag.getCompound("contains");

        tag.putBoolean("using", true);

        if(contains.getBoolean(Spyglass)){
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SPYGLASS_USE, SoundSource.PLAYERS,
                    1.0F, 1.0F);
        }

        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() == ModBlocks.SPRING.asItem();
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
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
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag contains = tag.getCompound("contains");
        return contains.getBoolean(Spyglass);
    }

    public boolean isSpyglass(ComputeFovModifierEvent event){
        ItemStack stack = event.getPlayer().getUseItem();
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag contains = tag.getCompound("contains");
        return contains.getBoolean(Spyglass);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        CompoundTag tag = stack.getOrCreateTag();
        int Springs_rn = tag.getInt("Springs_rn");
        float Stored = tag.getFloat("Stored");
        tag.putBoolean("using", false);
        float power = 1.0F * (Stored / CreateSprings.SPRING_CAPACITY);
        int time = getUseDuration(stack) - timeLeft;
        CompoundTag contains = tag.getCompound("contains");

        if (!level.isClientSide && time > 10 && Springs_rn == 2) {
            SpringProjectile projectile = (SpringProjectile) createProjectile(level, stack, entity);

            projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, power * 3.0F, 1.0F);

            if((entity instanceof Player player)){
                if(!player.isCreative()){
                    tag.putInt("Springs_rn", 1);
                    tag.putFloat("Stored", 0);
                }
            }

            level.addFreshEntity(projectile);

            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS,
                    1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

        } else if(!level.isClientSide && time > 10 && contains.getBoolean(BlockAmmo)){
            SpringAlloyBlockProjectile projectile = (SpringAlloyBlockProjectile) createBlockProjectile(level, stack, entity);
            projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, power * 3.0F, 1.0F);

            if((entity instanceof Player player)) {
                if (!player.isCreative()) {
                    contains.putBoolean(BlockAmmo, false);
                }
            }

            level.addFreshEntity(projectile);

            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS,
                    1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        CompoundTag tag = oldStack.getOrCreateTag();
        CompoundTag contains = tag.getCompound("contains");

        if (tag.getBoolean("using") && contains.getBoolean(Spyglass)){
            return true;
        }
        return !ItemStack.isSameItem(oldStack, newStack);
    }


    @Override
    public int getUseDuration(ItemStack stack) {
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

        if (core.addStackedLogick(ModBlocks.SPRING_ALLOY_BLOCK.get().asItem(), stack1, stack2, action, player)){
            return true;
        }

        if (core.addStackedLogick(Items.SPYGLASS, stack1, stack2, action, player)){
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
}
